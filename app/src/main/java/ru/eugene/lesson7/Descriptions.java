package ru.eugene.lesson7;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.zip.Inflater;

import ru.eugene.lesson7.db.FeedDataSource;
import ru.eugene.lesson7.db.NewsDataSource;
import ru.eugene.lesson7.db.RssProvider;
import ru.eugene.lesson7.download.DownloadService;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Descriptions extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;
    private int idFeed;
    private String url;
    private Context context;
    private HashMap<Integer, Boolean> posToIsRead = new HashMap<>();
    private HashMap<Integer, String> posToLink = new HashMap<>();
    private HashMap<Integer, Integer> posToId = new HashMap<>();
    ProgressDialog progress;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getBooleanExtra(DownloadService.RESULT, false)) {
                Toast.makeText(context, "Bad url", Toast.LENGTH_SHORT).show();
                finish();
            }
            progress.dismiss();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String from[] = new String[]{NewsDataSource.COLUMN_TITLE, NewsDataSource.COLUMN_PUB_DATE};
        int to[] = new int[]{android.R.id.text1, android.R.id.text2};
        adapter = new SimpleCursorAdapter(this, R.layout.descriptions_item, null, from, to, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View result = super.getView(position, convertView, parent);
                if (posToIsRead.get(position) == true) {
                    result.setAlpha(0.5f);
                } else {
                    result.setAlpha(1);
                }
                return result;
            }
        };
        getListView().setAdapter(adapter);

        idFeed = getIntent().getIntExtra(FeedDataSource.COLUMN_ID, -1);
        url = getIntent().getStringExtra(DownloadService.URL_);
        context = this;

        getLoaderManager().initLoader(1, null, this);
        progress = ProgressDialog.show(this, "downloading feeds", "wait please", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ContentValues values = new ContentValues();
        values.put(NewsDataSource.COLUMN_IS_READ, 1);

        getContentResolver().update(RssProvider.CONTENT_URI_NEWS, values,
                NewsDataSource.COLUMN_ID + "=?", new String[]{Integer.toString(posToId.get(position))});

        Intent viewInfo = new Intent(this, ViewInfo.class);
        viewInfo.putExtra(ViewInfo.URL_, posToLink.get(position));
        startActivity(viewInfo);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, RssProvider.CONTENT_URI_NEWS, NewsDataSource.getProjection(),
                NewsDataSource.COLUMN_ID_FEED + "=?", new String[]{Integer.toString(idFeed)}, NewsDataSource.COLUMN_ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (data.getCount() == 0) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(NewsDataSource.COLUMN_ID_FEED, idFeed);
            intent.putExtra(DownloadService.URL_, url);
            startService(intent);
        } else if (data.moveToFirst()) {
            int pos = 0;
            do {
                posToIsRead.put(pos, (data.getInt(data.getColumnIndex(NewsDataSource.COLUMN_IS_READ)) == 1));
                posToLink.put(pos, data.getString(data.getColumnIndex(NewsDataSource.COLUMN_LINK)));
                posToId.put(pos, data.getInt(data.getColumnIndex(NewsDataSource.COLUMN_ID)));
                pos++;
            } while (data.moveToNext());
            progress.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_descriptions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(NewsDataSource.COLUMN_ID_FEED, idFeed);
            intent.putExtra(DownloadService.URL_, url);
            startService(intent);
            progress.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
