package wj.com.recyclerviewdemo.adapter;/**
 * Created by wangjiang on 2016/4/7.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wj.com.recyclerviewdemo.R;

/**
 * User: WangJiang(wangjiang7747@gmail.com)
 * Date: 2016-04-07
 * Time: 19:45
 */
public class ListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;

    private List<String> mDatas;

    public ListAdapter() {
        int size = 30;
        mDatas = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mDatas.add("Item: " + i);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textView.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_10dp), 0, 0, 0);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.textSize_18sp));
        int height = context.getResources().getDimensionPixelSize(R.dimen.size_50dp);
        if (i == VIEW_TYPE_ONE) {
            textView.setBackgroundColor(Color.WHITE);
            height = context.getResources().getDimensionPixelSize(R.dimen.size_50dp);
        } else if (i == VIEW_TYPE_TWO) {
            textView.setBackgroundColor(Color.GREEN);
            height = context.getResources().getDimensionPixelSize(R.dimen.size_35dp);
        }
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height);
        textView.setLayoutParams(params);
        return new ViewCache(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewCache viewCache = (ViewCache) viewHolder;
        if (i % 5 == 0) {
            viewCache.textView.setText("Title " + i / 5);
        } else {
            viewCache.textView.setText(mDatas.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 5 == 0) return VIEW_TYPE_TWO;
        return VIEW_TYPE_ONE;
    }

    public static class ViewCache extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewCache(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
