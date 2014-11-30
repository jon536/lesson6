package ru.eugene.listviewpractice2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.eugene.listviewpractice2.Feeds;
import ru.eugene.listviewpractice2.dataBase.DataSourceFeeds;
import ru.eugene.listviewpractice2.dataBase.DbHelper;

/**
 * Created by eugene on 11/16/14.
 */
public class HandlerFeeds {
    private SQLiteDatabase database;
    private DataSourceFeeds dataSource;
    private DbHelper dbHelper;
    private ArrayList<String> feeds = new ArrayList<String>();
    private ArrayList<String> urlFeeds = new ArrayList<String>();

    private ArrayAdapter adapter;
    private Context context;
    private ListView listView;

    public HandlerFeeds(Context context, ListView listView) {
        clearData();
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        dataSource = new DataSourceFeeds(database);
        adapter = new ArrayAdapter(context,
                android.R.layout.simple_list_item_1, feeds);
        this.context = context;
        this.listView = listView;
    }

    public void updDataAdapter(List<Feeds> data) {
        clearData();
        for (Feeds it : data) {
            feeds.add(it.getTitle());
            urlFeeds.add(it.getLink());
        }
        listView.setAdapter(adapter);
    }

    private void clearData() {
        clearList(feeds);
        clearList(urlFeeds);
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

    public DataSourceFeeds getDataSource() {
        return dataSource;
    }

    public ArrayList<String> getFeeds() {
        return feeds;
    }

    public ArrayList<String> getUrlFeeds() {
        return urlFeeds;
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }


    public void setListAdapter() {
        if (feeds.isEmpty())
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String[]{"add new feed"});
        else if (adapter == null)
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, feeds);

        listView.setAdapter(adapter);
    }
}
