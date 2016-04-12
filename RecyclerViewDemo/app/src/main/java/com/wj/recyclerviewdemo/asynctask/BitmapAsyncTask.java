package com.wj.recyclerviewdemo.asynctask;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.wj.recyclerviewdemo.util.BitmapUtil;


/**
 * 该类主要用作于加载图片
 */
public class BitmapAsyncTask extends AsyncTask<LruCache<String, Bitmap>, Void, Bitmap> {
    private final int KEY = (int) System.currentTimeMillis();
    private int[] mWH;
    private String mUri;
    private ImageView mPiture;

    /**
     * @param picture 要显示图片的ImageView
     * @param uri     图片本地路径
     * @param wh      图片的宽高
     */
    public BitmapAsyncTask(ImageView picture, String uri, int[] wh) {
        if (picture == null) throw new NullPointerException("The ImageView is Null!");
        mPiture = picture;
        //防止错位显示图片
        mUri = uri;
        mPiture.setTag(KEY, mUri);
        mWH = wh;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null && mUri.equals(mPiture.getTag(KEY))) {
            mPiture.setImageBitmap(result);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected Bitmap doInBackground(@SuppressWarnings("unchecked") LruCache<String, Bitmap>... params) {
        Bitmap bitmap = BitmapUtil.compress(mPiture.getContext(), mUri, mWH[0], mWH[1]);
        if (bitmap != null) {
            params[0].put(mUri, bitmap);
        }
        return bitmap;
    }

}
