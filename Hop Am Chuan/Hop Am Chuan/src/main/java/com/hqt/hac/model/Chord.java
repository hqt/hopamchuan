package com.hqt.hac.model;

import java.io.Serializable;

public class Chord implements Serializable {

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
}
