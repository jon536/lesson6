package ru.eugene.lesson7;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import java.util.HashMap;

import ru.eugene.lesson7.db.FeedDataSource;
import ru.eugene.lesson7.db.NewsDataSource;
import ru.eugene.lesson7.db.RssProvider;
import ru.eugene.lesson7.download.DownloadService;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CONTEXT_MENU_EDIT = 10;
    private static final int CONTEXT_MENU_DELETE = 20;
    Context context;
    SimpleCursorAdapter adapter;
    private Cursor cursor;
    private View inflatedView;
    private HashMap<Integer, Integer> posToFeedId = new HashMap<>();
    private HashMap<Integer, String> posToUrl = new HashMap<>();
    private HashMap<Integer, String> posToName = new HashMap<>();
    private HashMap<Integer, Integer> posToId = new HashMap<>();
    private ListView listView;
    private int posOfSelectedEl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        String[] fromString = new String[]{FeedDataSource.COLUMN_NAME};
        int[] to = new int[]{android.R.id.text1};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, fromString, to, 0);
        listView = getListView();
        listView.setAdapter(adapter);

        getLoaderManager().initLoader(1, null, this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(listView);
                posOfSelectedEl = position;
                openContextMenu(listView);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new) {
            onCreateDialog(null).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("What do you want to do?");
        menu.add(0, CONTEXT_MENU_EDIT, 0, "edit");
        menu.add(0, CONTEXT_MENU_DELETE, 0, "delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CONTEXT_MENU_DELETE) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete feed")
                    .setMessage("Are you sure you want to delete this feed?")
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getContentResolver().delete(RssProvider.CONTENT_URI_FEED, FeedDataSource.COLUMN_ID + "=?",
                                    new String[]{Integer.toString(posToId.get(posOfSelectedEl))});
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else { //edit
            onCreateDialog(new Integer(posOfSelectedEl)).show();
        }

        return super.onContextItemSelected(item);
    }

    public Dialog onCreateDialog(final Integer position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        inflatedView = inflater.inflate(R.layout.dialog_insert, null);
        final EditText name = (EditText) inflatedView.findViewById(R.id.name);
        final EditText url = (EditText) inflatedView.findViewById(R.id.url);
        if (position != null) {
            name.setText(posToName.get(position));
            url.setText(posToUrl.get(position));
        }

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflatedView)
                // Add action buttons
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ContentValues values = new ContentValues();
                        values.put(FeedDataSource.COLUMN_NAME, name.getText().toString());
                        values.put(FeedDataSource.COLUMN_LINK, url.getText().toString());
                        if (position == null) {
                            context.getContentResolver().insert(RssProvider.CONTENT_URI_FEED, values);
                        } else {
                            context.getContentResolver().update(RssProvider.CONTENT_URI_FEED, values,
                                    NewsDataSource.COLUMN_ID + "=?", new String[]{Integer.toString(posToId.get(position))});
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent descriptions = new Intent(this, Descriptions.class);
        descriptions.putExtra(FeedDataSource.COLUMN_ID, posToFeedId.get(position));
        descriptions.putExtra(DownloadService.URL_, posToUrl.get(position));
        startActivity(descriptions);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(context, RssProvider.CONTENT_URI_FEED, FeedDataSource.getProjection(), null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (data.getCount() == 0) {
            int n = 4;
            ContentValues[] values = new ContentValues[n];
            for (int i = 0; i < n; i++)
                values[i] = new ContentValues();

            values[0].put(FeedDataSource.COLUMN_NAME, "bash");
            values[0].put(FeedDataSource.COLUMN_LINK, "http://bash.im/rss");

            values[1].put(FeedDataSource.COLUMN_NAME, "msk");
            values[1].put(FeedDataSource.COLUMN_LINK, "http://echo.msk.ru/interview/rss-fulltext.xml");

            values[2].put(FeedDataSource.COLUMN_NAME, "bbc");
            values[2].put(FeedDataSource.COLUMN_LINK, "http://feeds.bbci.co.uk/news/rss.xml");

            values[3].put(FeedDataSource.COLUMN_NAME, "lenta");
            values[3].put(FeedDataSource.COLUMN_LINK, "http://lenta.ru/rss");
            getContentResolver().bulkInsert(RssProvider.CONTENT_URI_FEED, values);
        }
        if (data.moveToFirst()) {
            int pos = 0;
            do {
                posToFeedId.put(pos, data.getInt(data.getColumnIndex(FeedDataSource.COLUMN_ID)));
                posToUrl.put(pos, data.getString(data.getColumnIndex(FeedDataSource.COLUMN_LINK)));
                posToName.put(pos, data.getString(data.getColumnIndex(FeedDataSource.COLUMN_NAME)));
                posToId.put(pos, data.getInt(data.getColumnIndex(FeedDataSource.COLUMN_ID)));
                pos++;
            } while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
