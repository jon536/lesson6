package ru.eugene.listviewpractice2;

/**
 * Created by eugene on 11/4/14.
 */
public class Feeds {
    private String title = "\"\"";
    private String link = "\"\"";
    private int id;

    public Feeds() {
    }

    public Feeds(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public void setLink(String link) {
        this.link = link;
    }
}
