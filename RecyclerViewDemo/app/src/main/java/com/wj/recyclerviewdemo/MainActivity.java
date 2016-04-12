package com.wj.recyclerviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wj.recyclerviewdemo.util.DisplayUtil;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String[] mTitles;
    private int[] mTypes = {RecyclerViewActivity.TYPE_LIST, RecyclerViewActivity.TYPE_GRID, RecyclerViewActivity.TYPE_STAGGERED_GRID_HORIZONTAL, RecyclerViewActivity.TYPE_STAGGERED_GRID_VERTICAL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getContentView() {
        mTitles = getResources().getStringArray(R.array.titles);
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0, length = mTitles.length; i < length; i++) {
            TextView textView = new TextView(this);
            textView.setTag(Integer.valueOf(mTypes[i]));
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.size_50dp)));
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_10dp), 0, 0, 0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.textSize_18sp));
            textView.setBackgroundDrawable(DisplayUtil.getSelectableItemBackground(this));
            textView.setOnClickListener(this);
            textView.setText(mTitles[i]);
            layout.addView(textView);
            View view = new View(this);
            view.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.line_1dp)));
            layout.addView(view);
        }
        return layout;
    }

    @Override
    public void onClick(View v) {
        Integer tag = (Integer) v.getTag();
        if (tag != null) {
            int type = tag.intValue();
            for (int i = 0, length = mTypes.length; i < length; i++) {
                if (mTypes[i] == type) {
                    Intent intent = new Intent(this, RecyclerViewActivity.class);
                    intent.putExtra(RecyclerViewActivity.TYPE, type);
                    intent.putExtra(RecyclerViewActivity.TITLE, mTitles[i]);
                    startActivity(intent);
                }
            }
        }

    }
}
