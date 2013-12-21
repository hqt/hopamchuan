package com.hqt.hac.helper.adapter;

/**
 * because there are multi implementations of ChordViewAdapter (because vast of platform)
 * we should create a common interface so both of them can call same methods
 */
public interface IChordView {
    public void setChordList(String[] chords);
}
