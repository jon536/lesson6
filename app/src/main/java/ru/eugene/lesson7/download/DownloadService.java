package ru.eugene.lesson7.download;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ru.eugene.lesson7.DescriptionItem;
import ru.eugene.lesson7.db.NewsDataSource;
import ru.eugene.lesson7.db.RssProvider;

/**
 * Created by eugene on 12/23/14.
 */
public class DownloadService extends IntentService {
    public static final String NOTIFICATION = "DownloadService";
    public static final String RESULT = "result";
    public static final boolean RESULT_OK = true;
    public static final boolean RESULT_FAIL = false;
    public static final String URL_ = "url";
    private List<DescriptionItem> itemsData;
    private HandlerRSS handlerRSS;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent result = new Intent(NOTIFICATION);
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();

            InputSource is = downloadUrl(intent.getStringExtra(URL_));

            int idFeed = intent.getIntExtra(NewsDataSource.COLUMN_ID_FEED, -1);
            Cursor previousData = getContentResolver().query(RssProvider.CONTENT_URI_NEWS, NewsDataSource.getProjection(),
                    NewsDataSource.COLUMN_ID_FEED + "=?", new String[] {idFeed + ""}, NewsDataSource.COLUMN_ID + " DESC");
            String lastPubDate = "";
            if (previousData.moveToFirst()) {
                lastPubDate = previousData.getString(previousData.getColumnIndex(NewsDataSource.COLUMN_PUB_DATE));
            }

            handlerRSS = new HandlerRSS(lastPubDate);
            try {
                saxParser.parse(is, handlerRSS);
            } catch (FinishParseException e) {}


            itemsData = handlerRSS.getItemsData();
            if (itemsData.size() == 0) {
                result.putExtra(RESULT, RESULT_OK);
                sendBroadcast(result);
                return;
            }

            ContentValues[] arrForInsert = new ContentValues[itemsData.size()];
            for (int i = itemsData.size() - 1; i >= 0; i--) {
                DescriptionItem curItem = itemsData.get(i);
                curItem.setIdFeed(idFeed);
                curItem.setIsRead(0);
                arrForInsert[itemsData.size() - i - 1] = NewsDataSource.getContentValues(curItem);
            }

            getContentResolver().bulkInsert(RssProvider.CONTENT_URI_NEWS, arrForInsert);
            result.putExtra(RESULT, RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
            result.putExtra(RESULT, RESULT_FAIL);
        }
        sendBroadcast(result);
    }

    private InputSource downloadUrl(String myurl) throws IOException {
        InputStream inputStream = null;

        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(1000);
        conn.setConnectTimeout(1500);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        inputStream = conn.getInputStream();

        String contentType = conn.getHeaderField("Content-Type");
        String encoding = "utf-8";
        if (contentType != null && contentType.contains("charset=")) {
            Matcher matcher = Pattern.compile("charset=([^\\s]+)").matcher(contentType);
            matcher.find();
            encoding = matcher.group(1);
        }

        Reader isr = new InputStreamReader(inputStream, encoding);
        InputSource is = new InputSource(isr);

        return is;
    }
}
