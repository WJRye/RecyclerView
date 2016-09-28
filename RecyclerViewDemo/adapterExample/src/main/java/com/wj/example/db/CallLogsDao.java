package com.wj.example.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.Random;

/**
 * Author：王江 on 2016/9/23 17:35
 * Description:
 */
public class CallLogsDao {
    private DBOpenHelper mDBOpenHelper;

    private CallLogsDao(Context context) {
        mDBOpenHelper = new DBOpenHelper(context);
    }

    public static CallLogsDao getInstance(Context context) {
        return CallLogsDaoHolder.getInstance(context);
    }

    public void delete(long _id) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL("delete from callLogs where _id=" + _id);
        db.close();
    }

    public void update(long _id, String cacheName) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.execSQL("update callLogs set cacheName=? where _id=?", new String[]{cacheName, Long.toString(_id)});
        db.close();
    }

    private static class CallLogsDaoHolder {
        private static CallLogsDao INSTACNE = null;

        private static CallLogsDao getInstance(Context context) {
            if (INSTACNE == null) {
                INSTACNE = new CallLogsDao(context);
            }
            return INSTACNE;
        }

    }

    public void insert(int count) {
        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            SQLiteStatement sqLiteStatement = db.compileStatement("insert into callLogs values(?,?,?,?)");
            for (int i = 0; i < count; i++) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindNull(1);
                sqLiteStatement.bindString(2, "Rye" + new Random().nextInt(100));
                sqLiteStatement.bindString(3, "133455677");
                sqLiteStatement.bindLong(4, System.currentTimeMillis());
                sqLiteStatement.execute();
            }
            sqLiteStatement.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Cursor getCursor() {
        SQLiteDatabase db = mDBOpenHelper.getReadableDatabase();
        return db.rawQuery("select * from callLogs", null);
    }
}
