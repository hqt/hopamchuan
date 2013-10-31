package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;

import java.util.List;

public class PlaylistAdapter extends BaseAdapter {


    Context mContext;
    List<Playlist> playLists;

    public PlaylistAdapter(Context context) {
        this.mContext = context;
        this.playLists = PlaylistDataAccessLayer.getAllPlayLists();
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
        //ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {

        }
        else {
          //  holder = (ViewHolder) row.getTag();
        }

        return null;
    }
}
