package com.hac.android.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Chord implements Parcelable {

    public int id;
    public int chordId;
    public String name;
    public String relations;

    public Chord(int id, int chordId, String name) {
        this.id = id;
        this.chordId = chordId;
        this.name = name;
    }

    public Chord(int chordId, String name) {
        this.chordId = chordId;
        this.name = name;
    }

    /** constructor for parcelable interface using */
    public Chord(Parcel chord) {
        readFromParcel(chord);
    }

    @Override
    public String toString() {
        return "Chord{" +
                "id=" + id +
                ", chordId=" + chordId +
                ", name='" + name + '\'' +
                ", relations='" + relations + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object)this).getClass() != o.getClass()) return false;

        Chord chord = (Chord) o;

        if (chordId != chord.chordId) return false;
        if (!name.equals(chord.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    ////////////////////////////////////////////////////////////////////
    //////////////////// IMPLEMENT PARCELABLE MECHANISM ///////////////

    @Override
    public int describeContents() {
        return 0;
    }

    /**
         public int id;
         public int chordId;
         public String name;
         public String relations;
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(chordId);
        dest.writeString(name);
        dest.writeString(relations);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        chordId = in.readInt();
        name = in.readString();
        relations = in.readString();
    }

    /**
     * This class will be required during un-marshalling data store in Parcel, individually or as arrays
     * If not exist this class. Android Runtime will throw Exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Chord> CREATOR = new Parcelable.Creator<Chord>() {
        @Override
        public Chord createFromParcel(Parcel source) {
            return new Chord(source);
        }
        @Override
        public Chord[] newArray(int size) {
            return new Chord[size];
        }
    };
}
