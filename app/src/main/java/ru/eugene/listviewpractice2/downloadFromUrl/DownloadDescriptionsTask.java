package ru.eugene.listviewpractice2.downloadFromUrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ru.eugene.listviewpractice2.Descriptions;
import ru.eugene.listviewpractice2.FinishParseException;
import ru.eugene.listviewpractice2.MyAdapter;
import ru.eugene.listviewpractice2.R;
import ru.eugene.listviewpractice2.dataBase.DataSourceDescriptions;
import ru.eugene.listviewpractice2.dataBase.DbHelper;
import ru.eugene.listviewpractice2.showResults.ShowFeed;

/**
 * Created by eugene on 11/17/14.
 */
public class DownloadDescriptionsTask extends AsyncTask<String, Void, Void> {
    Boolean somethingGoWrong = false;
    HandlerRSS handlerRSS;

    List<Descriptions> itemsData = null;

    Context context;
    ListView listViewDescriptions;

    SQLiteDatabase database;
    DbHelper dbHelper;
    DataSourceDescriptions source;
    String curTitle;
    Boolean fetchDataFromDb = false;


    public DownloadDescriptionsTask(Context context, ListView descriptions, String curTitle) {
        this.context = context;
        this.listViewDescriptions = descriptions;
        this.curTitle = curTitle;
        dbHelper = new DbHelper(context);
        database = dbHelper.getReadableDatabase();
        source = new DataSourceDescriptions(database);
    }

    @Override
    protected Void doInBackground(String... urls) {

        Log.i("LOG", "in background" + urls[0]);
        // params comes from the execute() call: params[0] is the url.
        try {
            //if There are data in database
            itemsData = source.read(DataSourceDescriptions.COLUMN_TITLE + "=?", new String[]{curTitle},
                    null, null, DataSourceDescriptions.COLUMN_PUB_DATE + " DESC");
            String lastTime = "";
            if (itemsData != null && itemsData.size() > 0) {
                fetchDataFromDb = true;
                lastTime = itemsData.get(0).getPubDate();
            }

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            handlerRSS = new HandlerRSS(fetchDataFromDb, lastTime);


            try {
                try {
                    saxParser.parse(downloadUrl(urls[0]), handlerRSS);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (FinishParseException e) {
                Log.i("LOG", "parsing was interrupted");
                for (Descriptions it : handlerRSS.getItemsData()) {
                    source.insert(it);
                }
                handlerRSS.getItemsData().addAll(itemsData);
            }
            itemsData = handlerRSS.getItemsData();
            if (!fetchDataFromDb) {
                for (Descriptions it : itemsData) {
                    source.insert(it);
                }
            }

            return null;
        } catch (Exception e) {
            somethingGoWrong = true;
            e.printStackTrace();

            return null;
        }
    }

    @Override
    protected void onPostExecute(Void a) {
        try {
            if (itemsData == null || itemsData.size() == 0) {
                finishWork();
            }

            ArrayList<String> descriptions = new ArrayList<String>();


            for (Descriptions it : itemsData) {
                descriptions.add(it.getDescription());
                it.setTitle(curTitle);
            }

            final MyAdapter myAdapter = new MyAdapter(context, R.layout.rowlayout, itemsData);
            this.listViewDescriptions.setAdapter(myAdapter);
            this.listViewDescriptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if (itemsData != null) {
                        Intent showFeed = new Intent(context, ShowFeed.class);
                        showFeed.putExtra("url", itemsData.get(position).getLink());
                        itemsData.get(position).setIsRead(1);
                        context.startActivity(showFeed);
                        source.update(itemsData.get(position));
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            finishWork();
        }
    }

    private void finishWork() {
        Toast toast;
        String message = "";
        if (somethingGoWrong) {
            message = "           bad url!\n" +
                    "Please try edit this feed!";
        } else {
            message = "something go wrong!\n" +
                    "Please try edit this feed!";
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        ((Activity) context).finish();
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


    public List<Descriptions> getItemsData() {
        return itemsData;
    }


}
