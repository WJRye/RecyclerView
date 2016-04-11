package wj.com.recyclerviewdemo.adapter;/**
 * Created by wangjiang on 2016/4/7.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import wj.com.recyclerviewdemo.R;
import wj.com.recyclerviewdemo.util.DisplayUtil;

/**
 * User: WangJiang(wangjiang7747@gmail.com)
 * Date: 2016-04-07
 * Time: 20:02
 */
public class GridAdapter extends BaseGridAdapter {


    public GridAdapter(Context context, RecyclerView recyclerView, int spanCount) {
        super(context, recyclerView, spanCount, GRID_VERTICAL);
    }


    @Override
    public int[] getWidthAndHeight(Context context, int spanCount) {
        int size = (DisplayUtil.getScreenWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.margin_2dp) * (spanCount * 2)) / spanCount;
        int[] wh = {size, size};
        return wh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        loadPicture(viewHolder);
    }
}
