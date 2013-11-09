package com.hqt.hac.model;

import android.os.Parcel;
import android.os.Parcelable;

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
    public String authorsStr = "";
    public String chordsStr = "";
    public String singersStr = "";

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

        // assign list of authors/chords/singers to string
        for (Artist author : authors) {
            authorsStr += author.artistName +",";
        }
        authorsStr = authorsStr.substring(0, authorsStr.length());

        for (Artist singer : singers) {
            singersStr += singer.artistName +",";
        }
        singersStr = singersStr.substring(0, singersStr.length());

        for (Chord chord : chords) {
            chordsStr += chord.name;
        }
        chordsStr = chordsStr.substring(0, chordsStr.length());
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
                ", authors=" + authors +
                ", chords=" + chords +
                ", singers=" + singers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Song song = (Song) o;

        if (songId != song.songId) return false;
        if (!content.equals(song.content)) return false;
        if (!firstLyric.equals(song.firstLyric)) return false;
        if (!link.equals(song.link)) return false;
        if (!title.equals(song.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + link.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + firstLyric.hashCode();
        return result;
    }
}
