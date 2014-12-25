package ru.eugene.lesson7;

/**
 * Created by eugene on 11/4/14.
 */
public class DescriptionItem {
    private String title = "";
    private String pubDate = "";
    private String link = "";
    private int isRead = 0;
    private int id;
    private int idFeed;

    public DescriptionItem() {
    }

    public DescriptionItem(String title, String link) {
        this.title = title;
        this.link = link;
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

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getIdFeed() {
        return idFeed;
    }

    public void setIdFeed(int idFeed) {
        this.idFeed = idFeed;
    }

}
