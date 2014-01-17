package com.hac.android.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hac.android.model.Playlist;
import com.hac.android.guitarchord.R;

import java.util.List;

public class PlaylistListAdapter extends BaseAdapter {

    Context mContext;
    /**
     * List all Songs of this favorite that adapter should be display
     */
    List<Playlist> playlists;

    public PlaylistListAdapter(Context context, List<Playlist> playlists) {
        this.mContext = context.getApplicationContext();
        this.playlists = playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return playlists.get(position).playlistId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_playlist_list, null);
            holder = new ViewHolder();
            holder.numberOfSongTxt = (TextView) row.findViewById(R.id.countSongText);
            holder.playListNameTxt = (TextView) row.findViewById(R.id.playlist);
            holder.descriptionTxt = (TextView) row.findViewById(R.id.description);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Playlist rowItem = (Playlist) getItem(position);
        holder.numberOfSongTxt.setText(rowItem.numberOfSongs + ""); // carefully when set text is number
        holder.playListNameTxt.setText(rowItem.playlistName);
        holder.descriptionTxt.setText(rowItem.playlistDescription);

        return row;
    }

    /**
     * ViewHolder pattern
     */
    private class ViewHolder {
        TextView numberOfSongTxt;
        TextView playListNameTxt;
        TextView descriptionTxt;
    }
}
