package ru.eugene.lesson7.db;

import android.content.ContentValues;

import ru.eugene.lesson7.DescriptionItem;

/**
 * Created by eugene on 12/23/14.
 */
public class NewsDataSource {
    public static final String TABLE = "news";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PUB_DATE = "pub_date";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_ID_FEED = "id_feed";
    public static final String COLUMN_IS_READ = "is_read";

    public static final String CREATE_TABLE = "create table " + TABLE +
            "( " + COLUMN_ID + " integer primary key autoincrement" +
            ", " + COLUMN_TITLE + " text not null" +
            ", " + COLUMN_PUB_DATE + " text not null" +
            ", " + COLUMN_ID_FEED + " integer not null" +
            ", " + COLUMN_LINK + " text not null" +
            ", " + COLUMN_IS_READ + " integer not null);";

    public static ContentValues getContentValues(DescriptionItem it) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, it.getTitle());
        values.put(COLUMN_PUB_DATE, it.getPubDate());
        values.put(COLUMN_LINK, it.getLink());
        values.put(COLUMN_ID_FEED, it.getIdFeed());
        values.put(COLUMN_IS_READ, it.getIsRead());
        return values;
    }

    public static String[] getProjection() {
        return new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_LINK, COLUMN_PUB_DATE, COLUMN_IS_READ};
    }
}
