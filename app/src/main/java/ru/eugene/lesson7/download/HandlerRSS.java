package ru.eugene.lesson7.download;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.eugene.lesson7.DescriptionItem;

/**
 * Created by eugene on 11/4/14.
 */
public class HandlerRSS extends DefaultHandler {
    private List<DescriptionItem> itemsData = null;
    private DescriptionItem descriptionItemObject = null;

    private Boolean item = false;
    private Boolean link = false;
    private Boolean title = false;
    private Boolean pubDate = false;

    private String textLinkToWebSite = "";
    private String textPubDate = "";
    private String textTitle = "";
    private String lastPubDate = "";

    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private boolean t = true;

    HandlerRSS() {}
    HandlerRSS(String lastPubDate) {
        this.lastPubDate = lastPubDate;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (item) {
            String temp = new String(ch, start, length);
            if (link) {
                textLinkToWebSite += temp;
            } else if (pubDate) {
                textPubDate += temp;
            } else if (title) {
                textTitle += temp;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (item) {
            if (qName.equalsIgnoreCase("item")) {
                itemsData.add(descriptionItemObject);
                item = false;
            } else if (qName.equalsIgnoreCase("link") && link) {
                link = false;
                descriptionItemObject.setLink(textLinkToWebSite);
                textLinkToWebSite = "";
            } else if (qName.equalsIgnoreCase("pubDate") && pubDate) {
                pubDate = false;
                descriptionItemObject.setPubDate(textPubDate);
                if (t) {
                    Log.i("LOG", textPubDate);
                    t = false;
                }
                if (textPubDate.equals(lastPubDate)) {
                    throw new FinishParseException();
                }
                textPubDate = "";
            } else if (qName.equalsIgnoreCase("title") && title) {
                title = false;
                descriptionItemObject.setTitle(textTitle);
                textTitle = "";
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (item) {
            if (qName.equalsIgnoreCase("link")) {
                link = true;
            } else if (qName.equalsIgnoreCase("pubDate")) {
                pubDate = true;
            } else if (qName.equalsIgnoreCase("title")) {
                title = true;
            }
        }

        if (qName.equalsIgnoreCase("item")) {
            descriptionItemObject = new DescriptionItem();
            if (itemsData == null)
                itemsData = new ArrayList<DescriptionItem>();
            item = true;
        }
    }

    public List<DescriptionItem> getItemsData() {
        return itemsData;
    }
}
