package ru.eugene.listviewpractice2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by eugene on 10/28/14.
 */
public class MyAdapter extends ArrayAdapter<Descriptions> {
    private static final String LOG = "DEBUG";
    List<Descriptions> data;
    int resource;
    Context context;
    int [] imagesId;

    public MyAdapter(Context context, int resource, List<Descriptions> data) {
        super(context, resource, data);
        this.data = data;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View row = convertView;
        if (row == null) {
            LayoutInflater myInflater = ((Activity) context).getLayoutInflater();
            row = myInflater.inflate(resource, parent, false);
            holder = new Holder();
            holder.data = (TextView) row.findViewById(R.id.text);
            holder.pubDate = (TextView) row.findViewById(R.id.pubDate);

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        Date inputDate = null;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

        Descriptions curDescription = data.get(position);
        holder.data.setText(Html.fromHtml(curDescription.getDescription()));
        if (curDescription.getIsRead() > 0) {
            holder.data.setTextColor(Color.GRAY);
        }

        try {
            inputDate = inputDateFormat.parse(curDescription.getPubDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("LOG", position + "  " + curDescription.getId() + "  : " + curDescription.getIsRead() + "");
        holder.pubDate.setText(outputDateFormat.format(inputDate));
        holder.pubDate.setTextColor(Color.GRAY);


        return row;
    }

    class Holder {
        TextView data;
        TextView pubDate;
    }
}