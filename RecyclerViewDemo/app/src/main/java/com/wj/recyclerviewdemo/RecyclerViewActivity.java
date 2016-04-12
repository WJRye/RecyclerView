package com.wj.recyclerviewdemo;/**
 * Created by wangjiang on 2016/4/7.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.wj.recyclerviewdemo.adapter.BaseGridAdapter;
import com.wj.recyclerviewdemo.adapter.GridAdapter;
import com.wj.recyclerviewdemo.adapter.ListAdapter;
import com.wj.recyclerviewdemo.adapter.StaggeredGridAdapter;
import com.wj.recyclerviewdemo.decorator.DividerItemDecoration;

/**
 * User: WangJiang(wangjiang7747@gmail.com)
 * Date: 2016-04-07
 * Time: 19:29
 */
public class RecyclerViewActivity extends BaseActivity {
    public static final int TYPE_LIST = 1;
    public static final int TYPE_GRID = 2;
    public static final int TYPE_STAGGERED_GRID_HORIZONTAL = 3;
    public static final int TYPE_STAGGERED_GRID_VERTICAL = 4;
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getIntent().getStringExtra(TITLE));
        initViews();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int type = getIntent().getIntExtra(TYPE, 0);
        RecyclerView.LayoutManager layoutManager = null;
        DividerItemDecoration dividerItemDecoration = null;
        switch (type) {
            case TYPE_LIST: {
                layoutManager = new LinearLayoutManager(this);
                dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.TYPE_LIST);
                mRecyclerView.addItemDecoration(dividerItemDecoration);
                mAdapter = new ListAdapter();
                break;
            }
            case TYPE_GRID: {
                int spanCount = 3;
                layoutManager = new GridLayoutManager(this, spanCount);
//                dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.TYPE_GRID);
//                mRecyclerView.addItemDecoration(dividerItemDecoration);
                mAdapter = new GridAdapter(this, mRecyclerView, spanCount);
                break;
            }
            case TYPE_STAGGERED_GRID_HORIZONTAL: {
                int spanCount = 4;
                layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL);
//                dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.TYPE_GRID);
//                mRecyclerView.addItemDecoration(dividerItemDecoration);
                mAdapter = new StaggeredGridAdapter(this, mRecyclerView, spanCount, BaseGridAdapter.STAGGERED_GRID_HORIZONTAL);
                break;
            }
            case TYPE_STAGGERED_GRID_VERTICAL: {
                int spanCount = 3;
                layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
                mAdapter = new StaggeredGridAdapter(this, mRecyclerView, spanCount, BaseGridAdapter.STAGGERED_GRID_VERTICAL);
                break;
            }
            default:
                break;
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.recyclerview, null);
    }
}
