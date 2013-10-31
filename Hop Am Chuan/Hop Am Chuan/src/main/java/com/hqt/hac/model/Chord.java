package com.hqt.hac.model;

import java.io.Serializable;

public class Chord implements Serializable {

    public int id;
    public int chordId;
    public String name;
    public String relations;

    public Chord(int id, int chordId, String name, String relations) {
        this.id = id;
        this.chordId = chordId;
        this.name = name;
        this.relations = relations;
    }

    public Chord(int chordId, String name, String relations) {
        this.chordId = chordId;
        this.name = name;
        this.relations = relations;
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
}
