package ru.eugene.listviewpractice2.dataBase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.eugene.listviewpractice2.Descriptions;

/**
 * Created by eugene on 11/10/14.
 */
public class DataSourceDescriptions extends DataSource<Descriptions> {
    public static final String TABLE_DESCRIPTIONS = "rss_descriptions";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTIONS = "description";
    public static final String COLUMN_PUB_DATE = "pub_date";
    public static final String COLUMN_IS_READ = "is_read";

    // Database creation sql statement
    public static final String CREATE_TABLE_DESCRIPTIONS = "create table " + TABLE_DESCRIPTIONS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text,"
            + COLUMN_LINK + " text,"
            + COLUMN_DESCRIPTIONS + " text, "
            + COLUMN_PUB_DATE + " text, "
            + COLUMN_IS_READ + " integer);";

    public DataSourceDescriptions(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public boolean insert(Descriptions entity) {
        if (entity == null) {
            return false;
        }
        long result = database.insert(TABLE_DESCRIPTIONS, null,
                generateContentValuesFromObject(entity));

        return result != -1;
    }

    @Override
    public boolean update(Descriptions entity) {
        if (entity == null) {
            return false;
        }
        long result = database.update(TABLE_DESCRIPTIONS, generateContentValuesFromObject(entity),
                COLUMN_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    @Override
    public boolean delete(Descriptions entity) {
        if (entity == null) {
            return false;
        }
        long result = database.delete(TABLE_DESCRIPTIONS, COLUMN_ID + " = " + entity.getId(), null);
        return result != 0;
    }

    @Override
    public List read() {
        Cursor cursor = database.query(TABLE_DESCRIPTIONS, getAllColumns(), null, null, null, null, null);
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

    @Override
    public List read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor cursor = database.query(TABLE_DESCRIPTIONS, getAllColumns(), selection, selectionArgs, groupBy, having, orderBy);
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
        return new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_LINK, COLUMN_DESCRIPTIONS, COLUMN_PUB_DATE, COLUMN_IS_READ};
    }

    private ContentValues generateContentValuesFromObject(Descriptions entity) {
        if (entity == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, entity.getTitle());
        values.put(COLUMN_LINK, entity.getLink());
        values.put(COLUMN_DESCRIPTIONS, entity.getDescription());
        values.put(COLUMN_PUB_DATE, entity.getPubDate());
        values.put(COLUMN_IS_READ, entity.getIsRead());
        return values;
    }

    private Descriptions generateObjectFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        Descriptions result = new Descriptions();
        result.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        result.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        result.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_LINK)));
        result.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTIONS)));
        result.setPubDate(cursor.getString(cursor.getColumnIndex(COLUMN_PUB_DATE)));
        result.setIsRead(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_READ)));
        return result;
    }

}
