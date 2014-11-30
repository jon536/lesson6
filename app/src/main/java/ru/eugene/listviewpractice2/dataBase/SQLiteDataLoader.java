package ru.eugene.listviewpractice2.dataBase;

import android.content.Context;
import android.content.Loader;

import java.util.List;


/**
 * Created by eugene on 11/11/14.
 */
public class SQLiteDataLoader<T> extends AbstractDataLoader<List<T>> {
    private DataSource<T> mDataSource;
    private String mSelection;
    private String[] mSelectionArgs;
    private String mGroupBy;
    private String mHaving;
    private String mOrderBy;

    public SQLiteDataLoader(Context context, DataSource dataSource, String selection, String[] selectionArgs,
                                String groupBy, String having, String orderBy) {
        super(context);
        mDataSource = dataSource;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
    }

    @Override
    protected List<T> buildList() {
        List<T> result = mDataSource.read(mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy);
        return result;
    }

    public void insert(T entity) {
        new InsertTask(this).execute(entity);
    }

    public void update(T entity) {
        new UpdateTask(this).execute(entity);
    }

    public void delete(T entity) {
        new DeleteTask(this).execute(entity);
    }


    private class DeleteTask extends ContentChangingTask<T, Void, Void> {
        DeleteTask(Loader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(T... params) {
            mDataSource.delete(params[0]);
            return null;
        }
    }

    private class UpdateTask extends ContentChangingTask<T, Void, Void> {
        UpdateTask(Loader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(T... params) {
            mDataSource.update(params[0]);
            return null;
        }
    }

    private class InsertTask extends ContentChangingTask<T, Void, Void> {
        InsertTask(Loader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(T... params) {
            mDataSource.insert(params[0]);
            return null;
        }
    }
}
