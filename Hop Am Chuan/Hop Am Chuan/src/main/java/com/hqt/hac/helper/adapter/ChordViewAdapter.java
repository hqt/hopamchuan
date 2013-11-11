package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hac_library.components.ChordSurfaceView;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ChordViewAdapter extends BaseAdapter {
    public static String TAG = makeLogTag(ChordViewAdapter.class);

    Context mContext;

    String[] chords;

    public ChordViewAdapter(Context mContext, String[] chords) {
        this.mContext = mContext;
        this.chords = chords;
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

       /* ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_song_favorite, null);
            holder = new ViewHolder();
            holder.txtSongName = (TextView) row.findViewById(R.id.txtSongName);
            holder.txtLyrics = (TextView) row.findViewById(R.id.txtLyrics);
            holder.txtChord = (TextView) row.findViewById(R.id.txtChord);
            holder.imgFavorite = (ImageView) row.findViewById(R.id.imageFavorite);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Song song = songs.get(position);
        holder.txtSongName.setText(song.title);
        holder.txtLyrics.setText(song.firstLyric);
        //holder.txtChord.setText(song.getChordString());

        return row;*/
    }

    public static class ViewHolder {
        ChordSurfaceView imageChord;
    }
}
