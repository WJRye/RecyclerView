package com.wj.adapter;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

/**
 * Author：王江 on 2016/9/23 12:06
 * <p>
 * Description：Adapter that exposes data from a {@link Cursor Cursor} to a
 * {@link RecyclerView RecyclerView} widget.
 * <p>
 * The Cursor must include a column named "_id" or this class will not work.
 * Additionally, using {@link android.database.MergeCursor} with this class will
 * not work if the merged Cursors have overlapping values in their "_id"
 * columns.
 */
public abstract class RecyclerViewCursorAdapter extends RecyclerView.Adapter implements Filterable {

    private boolean mDataValid;

    private boolean mAutoRequery;

    private Cursor mCursor;

    private int mRowIDColumn;

    private ChangeObserver mChangeObserver;

    private DataSetObserver mDataSetObserver;

    private FilterQueryProvider mFilterQueryProvider;

    /**
     * If set the adapter will call requery() on the cursor whenever a content change
     * notification is delivered. Implies {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     *
     * @deprecated This option is discouraged, as it results in Cursor queries
     * being performed on the application's UI thread and thus can cause poor
     * responsiveness or even Application Not Responding errors.  As an alternative,
     * use {@link android.app.LoaderManager} with a {@link android.content.CursorLoader}.
     */
    @Deprecated
    public static final int FLAG_AUTO_REQUERY = 0x01;

    /**
     * If set the adapter will register a content observer on the cursor and will call
     * {@link #onContentChanged()} when a notification comes in.  Be careful when
     * using this flag: you will need to unset the current Cursor from the adapter
     * to avoid leaks due to its registered observers.  This flag is not needed
     * when using a CursorAdapter with a
     * {@link android.content.CursorLoader}.
     */
    public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x02;


    public RecyclerViewCursorAdapter(Cursor c, boolean autoRequery) {
        init(c, autoRequery ? FLAG_AUTO_REQUERY : FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public RecyclerViewCursorAdapter(Cursor c, int flags) {
        init(c, flags);
    }

    private void init(Cursor c, int flags) {
        if ((flags & FLAG_AUTO_REQUERY) == FLAG_AUTO_REQUERY) {
            flags |= FLAG_REGISTER_CONTENT_OBSERVER;
            mAutoRequery = true;
        } else {
            mAutoRequery = false;
        }
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;

        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
        if ((flags & FLAG_REGISTER_CONTENT_OBSERVER) == FLAG_REGISTER_CONTENT_OBSERVER) {
            mChangeObserver = new ChangeObserver();
            mDataSetObserver = new MyDataSetObserver();
        } else {
            mChangeObserver = null;
            mDataSetObserver = null;
        }

        if (cursorPresent) {
            if (mChangeObserver != null) c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) c.registerDataSetObserver(mDataSetObserver);
        }
    }

    /**
     * Returns the cursor.
     *
     * @return the cursor.
     */
    public Cursor getCursor() {
        return mCursor;
    }


    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(holder, mCursor);
    }

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor);

    @Override
    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItemId(int)
     */
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }


    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there was not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        return swapCursor(newCursor, new Notify(Notify.CHANGED));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemChanged(int)
     */
    public Cursor swapCursorItemChanged(Cursor newCursor, int position) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_CHANGED, position, 1));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemChanged(int, Object)
     */
    public Cursor swapCursorItemChanged(Cursor newCursor, int position, Object payload) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_CHANGED, position, 1, payload));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemRangeChanged(int, int)
     */
    public Cursor swapCursorItemRangeChanged(Cursor newCursor, int positionStart, int itemCount) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_CHANGED, positionStart, itemCount));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemRangeChanged(int, int, Object)
     */
    public Cursor swapCursorItemRangeChanged(Cursor newCursor, int positionStart, int itemCount, Object payload) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_CHANGED, positionStart, itemCount, payload));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemInserted(int)
     */
    public Cursor swapCursorItemInserted(Cursor newCursor, int position) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_INSERTED, position, 1));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemRangeInserted(int, int)
     */
    public Cursor swapCursorItemRangeInserted(Cursor newCursor, int positionStart, int itemCount) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_INSERTED, positionStart, itemCount));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemRemoved(int)
     */
    public Cursor swapCursorItemRemoved(Cursor newCursor, int position) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_REMOVED, position, 1));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemRangeRemoved(int, int)
     */
    public Cursor swapCursorItemRangeRemoved(Cursor newCursor, int positionStart, int itemCount) {
        return swapCursor(newCursor, new Notify(Notify.ITEM_RANGE_REMOVED, positionStart, itemCount));
    }

    /**
     * @see RecyclerView.Adapter#notifyItemMoved(int, int)
     */
    public Cursor swapCursorItemMoved(Cursor newCursor, int fromPosition, int toPosition) {
        Notify notify = new Notify(Notify.ITEM_RANGE_MOVED);
        notify.setFromPosition(fromPosition);
        notify.setToPosition(toPosition);
        return swapCursor(newCursor, notify);
    }

    private Cursor swapCursor(Cursor newCursor, Notify notify) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            switch (notify.getType()) {
                case Notify.CHANGED:
                    notifyDataSetChanged();
                    break;
                case Notify.ITEM_RANGE_CHANGED:
                    notifyItemRangeChanged(notify.getPositionStart(), notify.getItemCount(), notify.getPayload());
                    break;
                case Notify.ITEM_RANGE_INSERTED:
                    notifyItemRangeInserted(notify.getPositionStart(), notify.getItemCount());
                    break;
                case Notify.ITEM_RANGE_REMOVED:
                    notifyItemRangeRemoved(notify.getPositionStart(), notify.getItemCount());
                    break;
                case Notify.ITEM_RANGE_MOVED:
                    notifyItemMoved(notify.getFromPosition(), notify.getToPosition());
                    break;
            }

        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            //notifyDataSetInvalidated();
        }
        return oldCursor;
    }


    /**
     * <p>Converts the cursor into a CharSequence. Subclasses should override this
     * method to convert their results. The default implementation returns an
     * empty String for null values or the default String representation of
     * the value.</p>
     *
     * @param cursor the cursor to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    /**
     * Runs a query with the specified constraint. This query is requested
     * by the filter attached to this adapter.
     * <p/>
     * The query is provided by a
     * {@link FilterQueryProvider}.
     * If no provider is specified, the current cursor is not filtered and returned.
     * <p/>
     * After this method returns the resulting cursor is passed to {@link #changeCursor(Cursor)}
     * and the previous cursor is closed.
     * <p/>
     * This method is always executed on a background thread, not on the
     * application's main thread (or UI thread.)
     * <p/>
     * Contract: when constraint is null or empty, the original results,
     * prior to any filtering, must be returned.
     *
     * @param constraint the constraint with which the query must be filtered
     * @return a Cursor representing the results of the new query
     * @see #getFilter()
     * @see #getFilterQueryProvider()
     * @see #setFilterQueryProvider(FilterQueryProvider)
     */
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }

        return mCursor;
    }


    /**
     * Returns the query filter provider used for filtering. When the
     * provider is null, no filtering occurs.
     *
     * @return the current filter query provider or null if it does not exist
     * @see #setFilterQueryProvider(FilterQueryProvider)
     * @see #runQueryOnBackgroundThread(CharSequence)
     */
    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    /**
     * Sets the query filter provider used to filter the current Cursor.
     * The provider's
     * {@link FilterQueryProvider#runQuery(CharSequence)}
     * method is invoked when filtering is requested by a client of
     * this adapter.
     *
     * @param filterQueryProvider the filter query provider or null to remove it
     * @see #getFilterQueryProvider()
     * @see #runQueryOnBackgroundThread(CharSequence)
     */
    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }

    /**
     * Called when the {@link ContentObserver} on the cursor receives a change notification.
     * The default implementation provides the auto-requery logic, but may be overridden by
     * sub classes.
     *
     * @see ContentObserver#onChange(boolean)
     */
    protected void onContentChanged() {
        if (mAutoRequery && mCursor != null && !mCursor.isClosed()) {
            mDataValid = mCursor.requery();
        }
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            //notifyDataSetInvalidated();
        }
    }

    private static class Notify {

        private static final int CHANGED = 0;
        private static final int ITEM_RANGE_CHANGED = 1;
        private static final int ITEM_RANGE_INSERTED = 2;
        private static final int ITEM_RANGE_REMOVED = 3;
        private static final int ITEM_RANGE_MOVED = 4;
        private int type = CHANGED;
        private int positionStart;
        private int itemCount;
        private Object payload;
        private int fromPosition;
        private int toPosition;

        public Notify(int type) {
            this.type = type;
        }

        public Notify(int type, int positionStart, int itemCount) {
            this(type, positionStart, itemCount, null);
        }

        public Notify(int type, int positionStart, int itemCount, Object payload) {
            this.type = type;
            this.positionStart = positionStart;
            this.itemCount = itemCount;
            this.payload = payload;
        }

        public int getPositionStart() {
            return positionStart;
        }

        public int getItemCount() {
            return itemCount;
        }

        public Object getPayload() {
            return payload;
        }

        public int getFromPosition() {
            return fromPosition;
        }

        public void setFromPosition(int fromPosition) {
            this.fromPosition = fromPosition;
        }

        public int getToPosition() {
            return toPosition;
        }

        public void setToPosition(int toPosition) {
            this.toPosition = toPosition;
        }

        public int getType() {
            return type;
        }
    }

}
