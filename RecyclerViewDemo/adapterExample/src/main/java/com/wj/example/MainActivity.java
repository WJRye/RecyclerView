package com.wj.example;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wj.example.db.CallLogsDao;

import example.com.wj.R;

public class MainActivity extends AppCompatActivity {
    private static final int TYPE_CP = 1;
    private static final int TYPE_DB = 2;
    private int mCurrentType = TYPE_CP;

    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MainAdapter(getCursor());
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemLongClickListener(new MainAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View v, int position) {
                new MenuDialogFragment().show(getSupportFragmentManager(), Integer.toString(position));
                return true;
            }
        });

    }


    /**
     * get cursor
     *
     * @return cursor
     */
    private Cursor getCursor() {
        Cursor c = null;
        if (mCurrentType == TYPE_CP) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    c = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, android.provider.CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls._ID}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
                }
            } else {
                c = getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls._ID}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            }
        } else {
            c = CallLogsDao.getInstance(this).getCursor();
        }
        return c;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call_logs_cp:
                mCurrentType = TYPE_CP;
                break;
            case R.id.call_logs_db:
                mCurrentType = TYPE_DB;
                break;
        }
        //close the old cursor
        mAdapter.swapCursor(getCursor()).close();
        return super.onOptionsItemSelected(item);
    }

    /**
     * delete the data of database
     *
     * @param _id the current row ID in the database
     */
    private void delete(long _id) {
        if (mCurrentType == TYPE_CP) {
            String where = "_id=" + _id;
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    getContentResolver().delete(CallLog.Calls.CONTENT_URI, where, null);
                }
            } else {
                getContentResolver().delete(CallLog.Calls.CONTENT_URI, where, null);
            }
        } else {
            CallLogsDao.getInstance(this).delete(_id);
        }
    }


    private void update(long _id, String cacheName) {
        if (mCurrentType == TYPE_CP) {
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.CACHED_NAME, cacheName);
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    getContentResolver().update(CallLog.Calls.CONTENT_URI, values, "_id=" + Long.toString(_id), null);
                }
            } else {
                getContentResolver().update(CallLog.Calls.CONTENT_URI, values, "_id=" + Long.toString(_id), null);
            }
        } else {
            CallLogsDao.getInstance(this).update(_id, cacheName);
        }
    }

    public static class MenuDialogFragment extends AppCompatDialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String[] items = getContext().getResources().getStringArray(R.array.menu);
            final int position = Integer.parseInt(getTag());
            for (int i = 0, len = items.length; i < len; i++) {
                items[i] = String.format(items[i], position);
            }
            return new AlertDialog.Builder(getActivity())
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity activity = (MainActivity) getActivity();
                            switch (which) {
                                case 0:
                                    activity.update(activity.mAdapter.getItemId(position), "notifyItemChanged");
                                    activity.mAdapter.swapCursorItemChanged(activity.getCursor(), position).close();
                                    break;
                                case 1:
                                    int changeItemCount = Integer.parseInt(String.valueOf(items[which].charAt(items[which].length() - 2)));
                                    for (int i = 0; i < changeItemCount; i++) {
                                        activity.update(activity.mAdapter.getItemId(position + i), "notifyItemRangeChanged");
                                    }
                                    activity.mAdapter.swapCursorItemRangeChanged(activity.getCursor(), position, changeItemCount).close();
                                    break;
                                case 2:
                                    activity.delete(activity.mAdapter.getItemId(position));
                                    activity.mAdapter.swapCursorItemRemoved(activity.getCursor(), position).close();
                                    break;
                                case 3:
                                    int removeItemCount = Integer.parseInt(String.valueOf(items[which].charAt(items[which].length() - 2)));
                                    for (int i = 0; i < removeItemCount; i++) {
                                        activity.delete(activity.mAdapter.getItemId(position + i));
                                    }
                                    activity.mAdapter.swapCursorItemRangeRemoved(activity.getCursor(), position, removeItemCount).close();
                                    break;
                                default:
                                    break;
                            }
                            dialog.dismiss();
                        }
                    })
                    .create();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            setStyle(STYLE_NO_TITLE, 0);
        }

    }


}
