package com.wj.example;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wj.adapter.RecyclerViewCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import example.com.wj.R;

public class MainAdapter extends RecyclerViewCursorAdapter {

    private OnItemLongClickListener mOnItemLongClickListener;

    public MainAdapter(Cursor cursor) {
        super(cursor, false);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);

        return new ViewCache(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mOnItemLongClickListener.onItemLongClick(v, holder.getAdapterPosition());
            }
        });
        ViewCache viewCache = (ViewCache) holder;
        viewCache.nameView.setText(cursor.getString(0));
        viewCache.numberView.setText(cursor.getString(1));
        viewCache.dateView.setText(new SimpleDateFormat("yy-MM-dd hh:mm:ss").format(new Date(cursor.getLong(2))));
    }


    public static class ViewCache extends RecyclerView.ViewHolder {

        private TextView nameView;
        private TextView numberView;
        private TextView dateView;

        public ViewCache(View itemView) {
            super(itemView);

            nameView = (TextView) itemView.findViewById(R.id.call_logs_name);
            numberView = (TextView) itemView.findViewById(R.id.call_logs_number);
            dateView = (TextView) itemView.findViewById(R.id.call_logs_date);
        }
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View v, int position);
    }

}