package com.hqt.hac.model;

import android.content.Context;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Playlist implements Serializable {

    public int id;
    public int playlistId;
    public String playlistName;
    public String playlistDescription;
    public Date date;
    public int isPublic;
    public int numberOfSongs = 0;
    private List<Integer> songIds;
    private List<Song> songs;

    /** because lazy loading. use public getter to process */
    public List<Integer> getAllSongIds(Context context) {
        if (songIds != null) return songIds;
        else {
            songs = PlaylistDataAccessLayer.getAllSongsFromPlaylist(context, playlistId);
            for (int i = 0; i < songs.size(); i++) {
                songIds.add(songs.get(i).id);
            }
            return songIds;
        }
    }

    /** because lazy loading. use public getter to process */
    public List<Song> getAllSongFromPlaylist(Context context) {
        if (songs != null) return songs;
        else {
            songs = PlaylistDataAccessLayer.getAllSongsFromPlaylist(context, playlistId);
            return songs;
        }
    }
    
    public Playlist(int id, int playlistId, String playlistName, String playlistDescription, Date date, int isPublic) {
        this.id = id;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.date = date;
        this.isPublic = isPublic;
    }

    public Playlist(int playlistId, String playlistName, String playlistDescription, Date date, int isPublic) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.date = date;
        this.isPublic = isPublic;
    }

    public Playlist(int id, int playlistId, String playlistName, String playlistDescription, Date date, int isPublic, int numberOfSongs) {
        this.id = id;
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.date = date;
        this.isPublic = isPublic;
        this.numberOfSongs = numberOfSongs;
    }

    public Playlist(int playlistId, String playlistName, String playlistDescription, Date date, int isPublic, int numberOfSongs) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.date = date;
        this.isPublic = isPublic;
        this.numberOfSongs = numberOfSongs;
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
                ", numberOfSongs=" + numberOfSongs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object)this).getClass() != o.getClass()) return false;

        Playlist playlist = (Playlist) o;

        if (isPublic != playlist.isPublic) return false;
        if (playlistId != playlist.playlistId) return false;
        if (!playlistDescription.equals(playlist.playlistDescription)) return false;
        if (!playlistName.equals(playlist.playlistName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = playlistName.hashCode();
        result = 31 * result + playlistDescription.hashCode();
        result = 31 * result + isPublic;
        return result;
    }
}
