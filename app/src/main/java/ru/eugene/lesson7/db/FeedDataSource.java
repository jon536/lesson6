package ru.eugene.lesson7.db;

/**
 * Created by eugene on 12/23/14.
 */
public class FeedDataSource {
    public static final String TABLE = "feed";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LINK = "link";

    public static final String CREATE_TABLE = "create table " + TABLE +
            "( " + COLUMN_ID + " integer primary key autoincrement" +
            ", " + COLUMN_NAME + " text not null" +
            ", " + COLUMN_LINK + " text not null);";
    private static String[] projection;

    public static String[] getProjection() {
        return new String[] {COLUMN_ID, COLUMN_NAME, COLUMN_LINK};
    }
}
