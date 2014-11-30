package ru.eugene.listviewpractice2.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.eugene.listviewpractice2.Descriptions;

/**
 * Created by eugene on 11/16/14.
 */
public class HandlerDescriptions {
    private SQLiteDatabase database;
    private DataSourceDescriptions dataSource;
    private DbHelper dbHelper;
    private ArrayList<String> feeds;
    private ArrayList<String> descriptions;
    private ArrayList<String> urlDescriptions;
    private ArrayList<String> pubDay;

    private ArrayAdapter adapter;
    private Context context;

    public HandlerDescriptions(Context context) {
        clearData();
        this.context = context;
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        dataSource = new DataSourceDescriptions(database);
        adapter = new ArrayAdapter(context,
                android.R.layout.simple_list_item_1);
    }

    public void updDataAdapter(List<Descriptions> data) {
        clearData();
        initData();
        for (Descriptions it : data) {
            feeds.add(it.getTitle());

            descriptions.add(it.getDescription());
            urlDescriptions.add(it.getLink());
            pubDay.add(it.getPubDate());
        }
        adapter.addAll(feeds);
    }

    private void initData() {
        if (feeds == null) {
            feeds = new ArrayList<String>();
        }

        if (descriptions == null) {
            descriptions = new ArrayList<String>();
        }

        if (urlDescriptions == null) {
            urlDescriptions = new ArrayList<String>();
        }

        if (pubDay == null) {
            pubDay = new ArrayList<String>();
        }
    }

    private void clearData() {
        clearList(feeds);
        clearList(descriptions);
        clearList(urlDescriptions);
        clearList(pubDay);
        feeds = descriptions = urlDescriptions = pubDay = null;
    }

    private void clearList(List<String> it) {
        if (it != null) {
            it.clear();
        }
    }

    public void destroy() {
        dbHelper.close();
        database.close();
        dataSource = null;
        dbHelper = null;
        database = null;
        clearData();
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public ArrayList<String> getFeeds() {
        return feeds;
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }
}
