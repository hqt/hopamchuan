package com.hqt.hac.model;

import com.hqt.hac.model.dao.PlaylistDataAccessLayer;

import java.io.Serializable;
import java.util.Date;

public class Playlist implements Serializable {

    public int id;
    public int playlistId;
    public String playlistName;
    public String playlistDescription;
    public Date date;
    public boolean isPublic;
    public int numberOfSongs;

    public Playlist(int id, int playlistId, String playlistName, String playlistDescription, Date date, boolean isPublic) {
        this.id = id;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.date = date;
        this.isPublic = isPublic;
        //numberOfSongs = PlaylistDataAccessLayer.getNumberOfSongsByPlaylist(id);
    }

    public Playlist(int playlistId, String playlistName, String playlistDescription, Date date, boolean isPublic) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.date = date;
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", playlistId=" + playlistId +
                ", playlistName='" + playlistName + '\'' +
                ", playlistDescription='" + playlistDescription + '\'' +
                ", date=" + date +
                ", isPublic=" + isPublic +
                '}';
    }


}
