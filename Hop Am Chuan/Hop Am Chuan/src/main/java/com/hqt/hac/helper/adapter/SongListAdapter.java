package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hqt.hac.helper.widget.DialogFactory;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;

public class SongListAdapter extends BaseAdapter {

    Activity activity;

    public interface RightMenuClick {
        public void onRightMenuClick(View view, Song song);
    }

    public RightMenuClick rightMenuClick;

    /**
     * List all Songs of this favorite that adapter should be display
     */
    List<Song> songs;

    public SongListAdapter(Activity activity, List<Song> songs) {
        this.activity = activity;
        this.songs = songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_song_songlist, null);
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

        final Song song = songs.get(position);
        holder.txtSongName.setText(song.title);
        holder.txtLyrics.setText(song.firstLyric.replace("\n", ""));
        holder.txtChord.setText(song.getChordString(activity.getApplicationContext()));

        if (song.isFavorite > 0) {
            holder.imgFavorite.setImageResource(R.drawable.star_liked);
        } else {
            holder.imgFavorite.setImageResource(R.drawable.star);
        }

        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightMenuClick.onRightMenuClick(view, song);
            }
        });
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
