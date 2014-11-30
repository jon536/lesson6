package ru.eugene.listviewpractice2.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eugene on 11/11/14.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "rssFeeds.db";
    public static final int DATABASE_VERSION = 18;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataSourceFeeds.CREATE_TABLE_FEEDS);
        db.execSQL(DataSourceDescriptions.CREATE_TABLE_DESCRIPTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DataSourceFeeds.TABLE_FEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + DataSourceDescriptions.TABLE_DESCRIPTIONS);
        onCreate(db);
    }
}
