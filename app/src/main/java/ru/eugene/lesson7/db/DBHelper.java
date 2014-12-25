package ru.eugene.lesson7.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eugene on 12/23/14.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String NAME = "rss.db";
    public static final int VERSION = 15;

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedDataSource.CREATE_TABLE);
        db.execSQL(NewsDataSource.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FeedDataSource.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NewsDataSource.TABLE);
        onCreate(db);
    }
}
