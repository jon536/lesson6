package ru.eugene.listviewpractice2.downloadFromUrl;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.eugene.listviewpractice2.Descriptions;
import ru.eugene.listviewpractice2.FinishParseException;

/**
 * Created by eugene on 11/4/14.
 */
public class HandlerRSS extends DefaultHandler {
    private List<Descriptions> itemsData = null;
    private Descriptions descriptionsObject = null;

    private Boolean item = false;
    private Boolean description = false;
    private Boolean link = false;
    private Boolean pubDate = false;
    private Boolean fetchDataFromDb;

    private String textDescription = "";
    private String textLinkToWebSite = "";
    private String textPubDate = "";

    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private String lastTime;


    HandlerRSS(Boolean fetchDataFromDb, String lastTime) {
        this.fetchDataFromDb = fetchDataFromDb;
        this.lastTime = lastTime;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (item) {
            String temp = new String(ch, start, length);
            if (link) {
                textLinkToWebSite += temp;
            } else if (description) {
                textDescription += temp;
            } else if (pubDate) {
                textPubDate += temp;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (item) {
            if (qName.equalsIgnoreCase("item")) {
                itemsData.add(descriptionsObject);
                item = false;
            } else if (qName.equalsIgnoreCase("description") && description) {
                description = false;
                descriptionsObject.setDescription(textDescription);
                textDescription = "";
            } else if (qName.equalsIgnoreCase("link") && link) {
                link = false;
                descriptionsObject.setLink(textLinkToWebSite);
                textLinkToWebSite = "";
            } else if (qName.equalsIgnoreCase("pubDate") && pubDate) {
                pubDate = false;
                String resultDate = "";
                try {
                    Date outputDate = inputDateFormat.parse(textPubDate);
                    resultDate = outputDateFormat.format(outputDate);

                    if (fetchDataFromDb && resultDate.equals(lastTime)) {
                        throw new FinishParseException();
                    }

                    descriptionsObject.setPubDate(outputDateFormat.format(outputDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                textPubDate = "";
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (item) {
            if (qName.equalsIgnoreCase("description")) {
                description = true;
            } else if (qName.equalsIgnoreCase("link")) {
                link = true;
            } else if (qName.equalsIgnoreCase("pubDate")) {
                pubDate = true;
            }
        }

        if (qName.equalsIgnoreCase("item")) {
            descriptionsObject = new Descriptions();
            if (itemsData == null)
                itemsData = new ArrayList<Descriptions>();
            item = true;
        }
    }

    public List<Descriptions> getItemsData() {
        return itemsData;
    }
}
