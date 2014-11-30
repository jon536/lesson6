package ru.eugene.listviewpractice2.dataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.eugene.listviewpractice2.Feeds;

/**
 * Created by eugene on 11/17/14.
 */
public class DataSourceFeeds extends DataSource<Feeds> {
    public static final String TABLE_FEEDS = "rss_feeds";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LINK = "link";

    public static final String CREATE_TABLE_FEEDS = "create table " + TABLE_FEEDS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text,"
            + COLUMN_LINK + " text);";

    public DataSourceFeeds(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public boolean insert(Feeds entity) {
        if (entity == null) {
            return false;
        }
        long result = database.insert(TABLE_FEEDS, null,
                generateContentValuesFromObject(entity));

        return result != -1;
    }

    @Override
    public boolean update(Feeds entity) {
        if (entity == null) {
            return false;
        }
        long result = database.update(TABLE_FEEDS, generateContentValuesFromObject(entity),
                COLUMN_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    @Override
    public boolean delete(Feeds entity) {
        if (entity == null) {
            return false;
        }
        long result = database.delete(TABLE_FEEDS, COLUMN_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    @Override
    public List read() {
        Cursor cursor = database.query(TABLE_FEEDS, getAllColumns(), null, null, null, null, null);
        List result = new ArrayList();
        Log.i("LOG", "in bef read" + (cursor != null) + " " + (cursor.moveToFirst()));
        if (cursor != null && cursor.moveToFirst()) {
            Log.i("LOG", "in read");
            while (!cursor.isAfterLast()) {
                result.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return result;
    }

    @Override
    public List read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor cursor = database.query(TABLE_FEEDS, getAllColumns(), selection, selectionArgs, groupBy, having, orderBy);
        List result = new ArrayList();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                result.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return result;
    }

    public String[] getAllColumns() {
        return new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_LINK};
    }

    private ContentValues generateContentValuesFromObject(Feeds entity) {
        if (entity == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, entity.getTitle());
        values.put(COLUMN_LINK, entity.getLink());
        return values;
    }

    private Feeds generateObjectFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        Feeds result = new Feeds();
        result.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        result.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        result.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_LINK)));
        return result;
    }

}
