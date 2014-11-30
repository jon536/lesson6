package ru.eugene.listviewpractice2.showResults;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ru.eugene.listviewpractice2.DetectConnection;
import ru.eugene.listviewpractice2.R;


public class ShowFeed extends Activity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_feed);
        if (!DetectConnection.checkInternetConnection(this)) {
            finishWork();
        }

        webView = (WebView) findViewById(R.id.showFeed); //This is the id you gave
        String url = getIntent().getStringExtra("url");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
    Log.i("LOG", "in show feed");
        webView.setWebViewClient(new WebViewClient() {
            public boolean timeout = true;
            public long delay = 5;

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("LOG", "in error");
                finishWork();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("LOG", "in page stop");
                timeout = false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("LOG", "in page start");
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            TimeUnit.SECONDS.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return timeout;
                    }

                    @Override
                    protected void onPostExecute(Boolean timeFinish) {
                        if (timeFinish)
                            finishWork();
                    }
                }.execute((Void[]) null);
            }
        });
        webView.loadUrl(url);
    }

    private void finishWork() {
        Toast.makeText(ShowFeed.this, "         Something go wrong! \n " +
                "Perhaps no network connection available", Toast.LENGTH_SHORT).show();
        ShowFeed.this.finish();
    }
}
