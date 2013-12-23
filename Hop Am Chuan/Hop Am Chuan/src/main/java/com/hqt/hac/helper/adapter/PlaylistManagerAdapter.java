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

import com.hqt.hac.helper.widget.DropdownPopup;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.List;

public class PlaylistManagerAdapter extends BaseAdapter {

    Context mContext;
    List<Playlist> playLists;

    public PlaylistManagerAdapter(Context context, List<Playlist> playlists) {
        this.mContext = context;
        this.playLists = playlists;
    }

    @Override
    public int getCount() {
        return playLists.size();
    }

    @Override
    public Object getItem(int position) {
        return playLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_playlist, null);
            holder = new ViewHolder();
            holder.numberOfSongTxt = (TextView) row.findViewById(R.id.countSongText);
            holder.playListNameTxt = (TextView) row.findViewById(R.id.playlist);
            holder.descriptionTxt = (TextView) row.findViewById(R.id.description);
            holder.optionBtn = (ImageView) row.findViewById(R.id.imageOptionView);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Playlist rowItem = (Playlist) getItem(position);
        holder.numberOfSongTxt.setText(rowItem.numberOfSongs + ""); // carefully when set text is number
        holder.playListNameTxt.setText(rowItem.playlistName);
        holder.descriptionTxt.setText(rowItem.playlistDescription);

        final PopupWindow pw = DropdownPopup.createPopup(inflater, R.layout.popup_song_detail_menu);

        holder.optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.showAsDropDown(view);
            }
        });

        return row;
    }

    private class ViewHolder {
        TextView numberOfSongTxt;
        TextView playListNameTxt;
        TextView descriptionTxt;
        ImageView optionBtn;
    }

    public static interface IPlaylistManagerAdapter {
        public void sharePlaylist();
        public void renamePlaylist();
        public void deletePlaylist();
    }
}
