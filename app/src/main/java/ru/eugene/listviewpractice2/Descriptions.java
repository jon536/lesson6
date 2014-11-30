package ru.eugene.listviewpractice2;

/**
 * Created by eugene on 11/4/14.
 */
public class Descriptions {
    private String title = "\"\"";
    private String description = "\"\"";
    private String pubDate = "\"\"";
    private String link = "\"\"";
    private int isRead = 0;
    private int id;

    public Descriptions() {
    }

    public Descriptions(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIsRead() {
        return isRead;
    }

    public String getLink() {
        return link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
