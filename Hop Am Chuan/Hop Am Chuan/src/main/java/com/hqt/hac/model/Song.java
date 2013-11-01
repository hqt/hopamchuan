package com.hqt.hac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Song implements Serializable {

    public int id = 0;
    public int songId;
    public String title;
    public String link;
    public String content;
    public String firstLyric;
    public Date date;
    public List<Artist> authors = new ArrayList<Artist>();
    public List<Chord> chords = new ArrayList<Chord>();
    public List<Artist> singers = new ArrayList<Artist>();

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

    public Song(int id, int songId, String title, String link, String content, String firstLyric, Date date, List<Artist> authors, List<Chord> chords, List<Artist> singers) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
        this.authors = authors;
        this.chords = chords;
        this.singers = singers;
    }

    public Song(int songId, String title, String link, String content, String firstLyric, Date date, List<Artist> authors, List<Chord> chords, List<Artist> singers) {
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
        this.authors = authors;
        this.chords = chords;
        this.singers = singers;
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
