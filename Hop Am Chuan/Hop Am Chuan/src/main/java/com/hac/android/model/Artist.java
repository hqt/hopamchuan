package com.hac.android.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.hac.android.model.dal.ArtistDataAccessLayer;


public class Artist implements Parcelable
{

    public int id;
    public int artistId;
    public String artistName;
    public String artistAscii;
    public int numOfSongs = -1;

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

    public Artist(Parcel artist) {
        readFromParcel(artist);
    }

    public int getNumOfSongs(Context context) {
        if (numOfSongs == -1) {
            numOfSongs = ArtistDataAccessLayer.getArtistSongsCount(context, artistId);
        }
        return numOfSongs;
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

    ////////////////////////////////////////////////////////////////////
    //////////////////// IMPLEMENT PARCELABLE MECHANISM ///////////////

    @Override
    public int describeContents() {
        return 0;
    }

    /**
         public int id;
         public int artistId;
         public String artistName;
         public String artistAscii;
      */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(artistId);
        dest.writeString(artistName);
        dest.writeString(artistAscii);

    }

    /**
     * must be assign again in-order
     */
    private void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.artistId = in.readInt();
        this.artistName = in.readString();
        this.artistAscii = in.readString();
    }

    /**
     * This class will be required during un-marshalling data store in Parcel, individually or as arrays
     * If not exist this class. Android Runtime will throw Exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }
        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
