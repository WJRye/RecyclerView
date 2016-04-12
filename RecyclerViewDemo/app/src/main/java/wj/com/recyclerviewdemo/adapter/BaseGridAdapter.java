package wj.com.recyclerviewdemo.adapter;/**
 * Created by wangjiang on 2016/4/7.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wj.com.recyclerviewdemo.R;
import wj.com.recyclerviewdemo.asynctask.BitmapAsyncTask;

/**
 * User: WangJiang(wangjiang7747@gmail.com)
 * Date: 2016-04-07
 * Time: 19:44
 */
public abstract class BaseGridAdapter extends RecyclerView.Adapter {

    //水平网格
    public static final int STAGGERED_GRID_HORIZONTAL = 0;
    //瀑布流
    public static final int STAGGERED_GRID_VERTICAL = 1;
    //垂直网格
    public static final int GRID_VERTICAL = 2;
    public int mOrientation = GRID_VERTICAL;
    //是否停止了滑动
    private boolean mIsIdle = true;
    //item 的宽高
    int[] mWH = null;
    private List<String> mUris = new ArrayList<>();
    //缓存图片
    private LruCache<String, Bitmap> mLruCache;
    private Set<BitmapAsyncTask> mBitmapAsyncTasks;

    public BaseGridAdapter(Context context, RecyclerView recyclerView, int spanCount, int orientation) {
        mOrientation = orientation;
        mWH = getWidthAndHeight(context, spanCount);
        mUris.addAll(getUris(context));
        mLruCache = new LruCache<String, Bitmap>((int) Runtime.getRuntime().maxMemory() / 8) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mBitmapAsyncTasks = new HashSet<>(mUris.size());
        addOnScrollListener(recyclerView);
    }

    public abstract int[] getWidthAndHeight(Context context, int spanCount);

    /**
     * 监听RecyclerView的滑动事件
     *
     * @param recyclerView
     */
    private void addOnScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mIsIdle = true;
                    int count = recyclerView.getChildCount();
                    for (int i = 0; i < count; i++) {
                        if (!mIsIdle) break;
                        View view = recyclerView.getChildAt(i);
                        loadPicture(recyclerView.getChildViewHolder(view));
                    }
                } else {
                    if (mIsIdle) {
                        mIsIdle = false;
                        int size = mBitmapAsyncTasks.size();
                        //在滑动的时候，取消未完成的任务
                        BitmapAsyncTask[] bats = mBitmapAsyncTasks.toArray(new BitmapAsyncTask[size]);
                        for (BitmapAsyncTask bat : bats) {
                            if (bat != null && bat.getStatus() != AsyncTask.Status.FINISHED) {
                                bat.cancel(true);
                                mBitmapAsyncTasks.remove(bat);
                                bat = null;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 获得系统图片的路径
     *
     * @param context
     * @return
     */
    private ArrayList<String> getUris(Context context) {
        ArrayList<String> uris = new ArrayList<>();
        //按照添加时间倒叙排序
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATE_ADDED}, null, null, "date_added desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                uris.add(cursor.getString(0));
            }
            cursor.close();
            cursor = null;
        }
        return uris;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(mWH[0], mWH[1]);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.margin_2dp);
        params.leftMargin = margin;
        params.topMargin = margin;
        params.rightMargin = margin;
        params.bottomMargin = margin;
        imageView.setLayoutParams(params);
        return new ViewCache(imageView);
    }

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i);

    /**
     * 加载图片
     *
     * @param viewHolder
     */
    protected void loadPicture(RecyclerView.ViewHolder viewHolder) {
        ViewCache viewCache = (ViewCache) viewHolder;
        String uri = mUris.get(viewHolder.getAdapterPosition());
        Bitmap bitmap = mLruCache.get(uri);
        if (bitmap == null) {
            viewCache.imageView.setImageResource(R.drawable.image_default_bg);
            if (mIsIdle) {
                BitmapAsyncTask bat = new BitmapAsyncTask(viewCache.imageView, uri, mWH);
                bat.execute(mLruCache);
                mBitmapAsyncTasks.add(bat);
            }
        } else {
            viewCache.imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return mUris.size();
    }

    public static class ViewCache extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewCache(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}
