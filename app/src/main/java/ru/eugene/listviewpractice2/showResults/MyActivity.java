package ru.eugene.listviewpractice2.showResults;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import ru.eugene.listviewpractice2.ChangeItem;
import ru.eugene.listviewpractice2.dataBase.DataSourceFeeds;
import ru.eugene.listviewpractice2.HandlerFeeds;
import ru.eugene.listviewpractice2.Feeds;
import ru.eugene.listviewpractice2.dataBase.SQLiteDataLoader;

/**
 * Created by eugene on 11/11/14.
 */
public class MyActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<List<Feeds>> {

    // The Loader's id (this id is specific to the ListFragment's LoaderManager)
    private static final int LOADER_ID = 1;
    private static final boolean DEBUG = true;
    private static final String TAG = "CustomLoaderExampleListFragment";
    List<Feeds> mainData;


    private int posOfSelectedEl;
    private Intent rssIntent;

    private ListView listView;
    private DataSourceFeeds source;

    private static final int CHANGE_ITEM_ADD = 777;
    private static final int CHANGE_ITEM_EDIT = 999;
    private static final int CONTEXT_MENU_ADD = 2;
    private static final int CONTEXT_MENU_EDIT = 3;
    private static final int CONTEXT_MENU_DELETE = 4;

    private HandlerFeeds hDataBaseFeeds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = (ListView) getListView();
        rssIntent = new Intent(this, RssFeed.class);
        hDataBaseFeeds = new HandlerFeeds(this, listView);
        source = hDataBaseFeeds.getDataSource();

        if (DEBUG) {
            Log.i(TAG, "+++ Calling initLoader()! +++");
            if (getLoaderManager().getLoader(LOADER_ID) == null) {
                Log.i(TAG, "+++ Initializing the new Loader... +++");
            } else {
                Log.i(TAG, "+++ Reconnecting with existing Loader (id '1')... +++");
            }
        }

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                registerForContextMenu(listView);
                posOfSelectedEl = position;
                openContextMenu(listView);
                return true;
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (hDataBaseFeeds.getUrlFeeds().isEmpty()) {
            addNewFeed();
            hDataBaseFeeds.setAdapter(null);
        } else {
            rssIntent.putExtra("url", hDataBaseFeeds.getUrlFeeds().get(position));
            rssIntent.putExtra("title", hDataBaseFeeds.getFeeds().get(position));
            startActivity(rssIntent);
        }
    }

    @Override
    public Loader<List<Feeds>> onCreateLoader(int id, Bundle args) {
        SQLiteDataLoader loader = new SQLiteDataLoader(this, hDataBaseFeeds.getDataSource(), null, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Feeds>> loader, List<Feeds> data) {
        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");
        if (data == null || data.size() == 0) {
            Log.i("LOG", "first feeds is empty");
            mainData = new ArrayList<Feeds>();
            mainData.add(new Feeds("bash", "http://bash.im/rss"));
            mainData.add(new Feeds("lenta", "http://lenta.ru/rss"));
            mainData.add(new Feeds("bbc", "http://feeds.bbci.co.uk/news/rss.xml"));
            for (Feeds feeds : mainData) {
                source.insert(feeds);
            }
        } else {
            mainData = data;
        }
        hDataBaseFeeds.updDataAdapter(mainData);
    }

    @Override
    public void onLoaderReset(Loader<List<Feeds>> listLoader) {
        hDataBaseFeeds.getAdapter().clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hDataBaseFeeds.destroy();
    }

    private void addNewFeed() {
        Intent changeItem = new Intent(this, ChangeItem.class);
        changeItem.putExtra("target", "add");
        startActivityForResult(changeItem, CHANGE_ITEM_ADD);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("What do you want to do?");
        menu.add(0, CONTEXT_MENU_ADD, 0, "add new feed");
        if (!hDataBaseFeeds.getFeeds().isEmpty()) {
            menu.add(0, CONTEXT_MENU_EDIT, 0, "edit");
            menu.add(0, CONTEXT_MENU_DELETE, 0, "delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CONTEXT_MENU_ADD) {
            addNewFeed();
        } else if (item.getItemId() == CONTEXT_MENU_DELETE) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete feed")
                    .setMessage("Are you sure you want to delete this feed?")
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            hDataBaseFeeds.getFeeds().remove(posOfSelectedEl);
                            hDataBaseFeeds.getUrlFeeds().remove(posOfSelectedEl);
                            source.delete(mainData.get(posOfSelectedEl));
                            mainData.remove(posOfSelectedEl);
                            hDataBaseFeeds.setListAdapter();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else { //edit
            Intent changeItem = new Intent(this, ChangeItem.class);
            changeItem.putExtra("target", "edit");
            changeItem.putExtra("name", hDataBaseFeeds.getFeeds().get(posOfSelectedEl));
            changeItem.putExtra("url", hDataBaseFeeds.getUrlFeeds().get(posOfSelectedEl));
            startActivityForResult(changeItem, CHANGE_ITEM_EDIT);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_ITEM_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name");
                String url = data.getStringExtra("url");

                hDataBaseFeeds.getFeeds().add(name);
                hDataBaseFeeds.getUrlFeeds().add(url);

                mainData.add(new Feeds(name, url));
                source.insert(mainData.get(mainData.size() - 1));

                hDataBaseFeeds.setListAdapter();
            }
        } else { //edit
            if (resultCode == Activity.RESULT_OK) {
                String name = data.getStringExtra("name");
                String url = data.getStringExtra("url");

                hDataBaseFeeds.getFeeds().set(posOfSelectedEl, name);
                hDataBaseFeeds.getUrlFeeds().set(posOfSelectedEl, url);

                mainData.get(posOfSelectedEl).setTitle(name);
                mainData.get(posOfSelectedEl).setLink(url);
                source.update(mainData.get(posOfSelectedEl));

                hDataBaseFeeds.setListAdapter();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("articles", hDataBaseFeeds.getFeeds());
        outState.putStringArrayList("urls", hDataBaseFeeds.getUrlFeeds());
    }
}
