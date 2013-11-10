package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.List;

public class FavoriteManagerAdapter extends BaseAdapter {

    Context mContext;

    /**
     * List all Songs of this favorite that adapter should be display
     */
    List<Song> songs;

    /** cache this image */
    public static ImageView favoriteStar;

    public FavoriteManagerAdapter(Context mContext, List<Song> songs) {
        this.mContext = mContext;
        this.songs = songs;
        //favoriteStar = mContext.getResources().getResourceName(R.id.search_button);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).songId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
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
        holder.txtChord.setText(song.getChordString());

        return row;
    }

    /**
     * ViewHolder pattern
     */
    private class ViewHolder {
        TextView txtSongName;
        TextView txtLyrics;
        TextView txtChord;
        ImageView imgFavorite;
    }
}
