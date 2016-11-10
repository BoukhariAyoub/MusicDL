package com.boukharist.musicdl.model;

/**
 * Created by Administrateur on 15-Feb-16.
 */
public class Item {

    private String title;
    private String url;
    private String length;
    private String error;
    private String status;


    public Item(String title, String link, String length) {
        this.title = title;
        this.url = link;
        this.length = length;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getLength() {
        return secondsToMinutes(length);
    }

    public String getError() {
        return error;
    }

    private String secondsToMinutes(String seconds) {
        int converted = Integer.valueOf(seconds);
        int min = converted / 60;
        int sec = converted % 60;
        String seco = sec < 10 ? "0" + sec : sec + "";
        return min + ":" + seco;
    }

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", length='" + length + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
