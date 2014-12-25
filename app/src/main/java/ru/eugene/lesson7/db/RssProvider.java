package ru.eugene.lesson7.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by eugene on 12/23/14.
 */
public class RssProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.eugene.lesson7.db";
    public static final String FEED_PATH = "feed_path";
    public static final String NEWS_PATH = "news_path";
    public static final int FEED = 10;
    public static final int NEWS = 20;
    public static final Uri CONTENT_URI_FEED = Uri.parse("content://" + AUTHORITY + "/" + FEED_PATH);
    public static final Uri CONTENT_URI_NEWS = Uri.parse("content://" + AUTHORITY + "/" + NEWS_PATH);
    private DBHelper db;
    private boolean isBulk = false;

    static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, FEED_PATH, FEED);
        sUriMatcher.addURI(AUTHORITY, NEWS_PATH, NEWS);
    }

    @Override
    public boolean onCreate() {
        db = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor result = database.query(getTable(uri), projection, selection, selectionArgs, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        Log.i("LOG", "getType");
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = db.getWritableDatabase();
        long resultId = database.insert(getTable(uri), null, values);
        if (resultId > 0) {
            if (!isBulk) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            uri = ContentUris.withAppendedId(uri, resultId);
        }
        database.close();
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int resultId = database.delete(getTable(uri), selection, selectionArgs);
        if (resultId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return resultId;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        isBulk = true;
        int cnt = 0;
        for (ContentValues it : values) {
            Uri newUri = insert(uri, it);
            if (!newUri.equals(uri)) {
                cnt++;
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        isBulk = false;
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int resultId = database.update(getTable(uri), values, selection, selectionArgs);
        if (resultId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return resultId;
    }

    private String getTable(Uri uri) {
        if (sUriMatcher.match(uri) == NEWS) {
            return NewsDataSource.TABLE;
        } else {
            return FeedDataSource.TABLE;
        }
    }
}
