package ru.eugene.listviewpractice2.showResults;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import ru.eugene.listviewpractice2.DetectConnection;
import ru.eugene.listviewpractice2.R;
import ru.eugene.listviewpractice2.downloadFromUrl.DownloadDescriptionsTask;

public class RssFeed extends Activity {
    public static final String LOG = "RssFeed";
    private ListView descriptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feed);
        descriptions = (ListView) findViewById(R.id.descriptions);

        String urlAddress = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        new DownloadDescriptionsTask(this, descriptions, title).execute(urlAddress);

    }
}
