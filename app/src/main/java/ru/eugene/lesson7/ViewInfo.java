package ru.eugene.lesson7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ru.eugene.lesson7.download.DetectConnection;


public class ViewInfo extends Activity {
    public static final String URL_ = "URL_";
    private WebView webView;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info);
        webView = (WebView) findViewById(R.id.webView);
        if (!DetectConnection.checkInternetConnection(this)) {
            finishWork();
        }
        progress = ProgressDialog.show(this, "downloading page", "wait please", true);

        String url = getIntent().getStringExtra(URL_);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean timeout = true;
            public long delay = 10;

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
                progress.dismiss();
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
        Toast.makeText(this , "         Something go wrong! \n " +
                "please try again!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

