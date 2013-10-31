package com.hqt.hac.model;

import java.io.Serializable;

public class Artist implements Serializable {

    public int id;
    public int artistId;
    public String artistName;
    public String artistAscii;

    public Artist(int id, int artistId, String artistName, String artistAscii) {
        this.id = id;
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistAscii = artistAscii;
    }

    public Artist(int artistId, String artistName, String artistAscii) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistAscii = artistAscii;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", artistAscii='" + artistAscii + '\'' +
                '}';
    }
}
