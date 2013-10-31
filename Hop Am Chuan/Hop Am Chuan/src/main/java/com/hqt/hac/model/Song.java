package com.hqt.hac.model;

import java.io.Serializable;
import java.util.Date;

public class Song implements Serializable {

    public int id;
    public int songId;
    public String title;
    public String link;
    public String content;
    public String firstLyric;
    public Date date;

    public Song(int id, int songId, String title, String link, String content, String firstLyric, Date date) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
    }

    public Song(int songId, String title, String link, String content, String firstLyric, Date date) {
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", songId=" + songId +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", content='" + content + '\'' +
                ", firstLyric='" + firstLyric + '\'' +
                ", date=" + date +
                '}';
    }
}
