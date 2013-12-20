package com.hqt.hac.model;

import com.hqt.hac.utils.StringUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object)this).getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (artistId != artist.artistId) return false;
        if (!artistAscii.equals(artist.artistAscii)) return false;
        if (!artistName.equals(artist.artistName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = artistName.hashCode();
        result = 31 * result + artistAscii.hashCode();
        return result;
    }
}
