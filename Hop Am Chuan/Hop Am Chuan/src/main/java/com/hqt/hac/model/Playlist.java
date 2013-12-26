package com.hqt.hac.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Playlist implements Parcelable {

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
            songIds = new ArrayList<Integer>();
            for (int i = 0; i < songs.size(); i++) {
                // TrungDQ: again, .songId, not .id
                // songIds.add(songs.get(i).id);
                songIds.add(songs.get(i).songId);
            }
            return songIds;
        }
    }

    public void setSongIds(List<Integer> ids) {
        this.songIds = ids;
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

    /** constructor for parcelable interface using */
    public Playlist(Parcel playlist) {
        readFromParcel(playlist);
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

    ////////////////////////////////////////////////////////////////////
    //////////////////// IMPLEMENT PARCELABLE MECHANISM ///////////////

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     public int id;
     public int playlistId;
     public String playlistName;
     public String playlistDescription;
     public Date date;
     public int isPublic;
     public int numberOfSongs = 0;
     private List<Integer> songIds;
     private List<Song> songs;
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(playlistId);
        dest.writeString(playlistName);
        dest.writeString(playlistDescription);
        dest.writeLong(date.getTime());
        dest.writeInt(isPublic);
        dest.writeInt(numberOfSongs);
        dest.writeList(songIds);
        dest.writeTypedList(songs);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        playlistId = in.readInt();
        playlistName = in.readString();
        playlistDescription = in.readString();
        date = new Date(in.readLong());
        isPublic = in.readInt();
        numberOfSongs = in.readInt();
        in.readList(songIds, Integer.class.getClassLoader());
        in.readTypedList(songs, Song.CREATOR);

    }

    /**
     * This class will be required during un-marshalling data store in Parcel, individually or as arrays
     * If not exist this class. Android Runtime will throw Exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }
        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
