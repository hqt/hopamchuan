package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.List;

public class PlaylistAdapter extends BaseAdapter {

    Context mContext;
    List<Playlist> playLists;

    public PlaylistAdapter(Context context) {
        this.mContext = context;
<<<<<<< HEAD
        this.playLists = PlaylistDataAccessLayer.getAllPlayLists(mContext);
=======
        this.playLists = PlaylistDataAccessLayer.getAllPlayLists(context);
>>>>>>> ffe60c4cfa796abc9d635a2bbb3d384b8008b00d
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
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Playlist rowItem = (Playlist) getItem(position);
        //   holder.numberOfSongTxt.setText(rowItem.);
        holder.playListNameTxt.setText(rowItem.playlistName);
        holder.descriptionTxt.setText(rowItem.playlistDescription);

        return row;
    }

    private class ViewHolder {
        TextView numberOfSongTxt;
        TextView playListNameTxt;
        TextView descriptionTxt;
    }
}
