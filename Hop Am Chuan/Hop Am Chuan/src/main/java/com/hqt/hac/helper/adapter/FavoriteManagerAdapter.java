package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hqt.hac.helper.widget.DialogFactory;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.view.R;

import java.util.List;

public class FavoriteManagerAdapter extends BaseAdapter {

    Activity activity;

    /**
     * List all Songs of this favorite that adapter should be display
     */
    List<Song> songs;

    /** cache this image */
    public static ImageView favoriteStar;

    public FavoriteManagerAdapter(Activity activity, List<Song> songs) {
        this.activity = activity;
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

        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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

        final Song song = songs.get(position);
        holder.txtSongName.setText(song.title);
        holder.txtLyrics.setText(song.firstLyric.replace("\n", ""));
        holder.txtChord.setText(song.getChordString(activity.getApplicationContext()));

        // Popup menu
        final PopupWindow pw = DialogFactory.createPopup(inflater, R.layout.popup_songlist_menu);

        // Popup menu item
        Button favoriteBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_addtofavorite);
        Button playlistBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_addtoplaylist);
        Button shareBtn = (Button) pw.getContentView().findViewById(R.id.song_list_menu_share);

        // Favorite button
        HacUtils.setFavoriteButtonEvent(activity.getApplicationContext(), song, favoriteBtn, pw);

        // "Add to playlist" dialog

        final Dialog dialog = DialogFactory.createDialog(activity, R.string.title_activity_chord_view,
                activity.getLayoutInflater(), R.layout.activity_setting);
        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
                dialog.show();
            }
        });

        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.showAsDropDown(view);
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
