package com.hqt.hac.helper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SectionIndexerAdapter extends BaseAdapter implements SectionIndexer, IChordView {

    public static String TAG = makeLogTag(SectionIndexerAdapter.class);

    /** String that using for SectionIndexer */
    private static String sections = "abcdefghilmnopqrstuvz";

    Context mContext;

    /** List all chords that adapter contains */
    String[] chords;

    /** currently index of chord */
    int[] index;

    public SectionIndexerAdapter(Context mContext, String[] chords) {
        this.mContext = mContext;
        this.chords = chords;
        index = new int[chords.length];
    }

    @Override
    public void setChordList(String[] chords) {
        this.chords = chords;
        index = new int[chords.length];
    }

    @Override
    public int getCount() {
        return chords.length;
    }

    @Override
    public Object getItem(int position) {
        return chords[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public Object[] getSections() {
        String[] sectionsArr = new String[sections.length()];
        for (int i=0; i < sections.length(); i++)
            sectionsArr[i] = "" + sections.charAt(i);
        return sectionsArr;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i=0; i < chords.length; i++) {
            String item = chords[i];
            if (item.charAt(0) == sections.charAt(section))
                return i;
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
