package ru.eugene.listviewpractice2.dataBase;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by eugene on 11/10/14.
 */
public abstract class DataSource<T> {
    protected SQLiteDatabase database;
    public DataSource(SQLiteDatabase database) {
        this.database = database;
    }
    public abstract boolean insert(T entity);
    public abstract boolean update(T entity);
    public abstract boolean delete(T entity);
    public abstract List read();
    public abstract List read(String selection, String[] selectionArgs,
                              String groupBy, String having, String orderBy);
}