package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.view.R;

import static com.hqt.hac.Utils.LogUtils.makeLogTag;

public class NavigationDrawerAdapter extends BaseAdapter {

    private static final String TAG = makeLogTag(NavigationDrawerAdapter.class);

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_CATEGORY = 2;
    private static final int TYPE_HEADER_PLAYLIST = 3;
    private static final int TYPE_PLAYLIST = 4;
    private static final int TYPE_MAX_COUNT = TYPE_PLAYLIST + 1;

    Context mContext;
    String[] categories;

    public NavigationDrawerAdapter(Context context) {
        this.mContext = context;
        categories = context.getResources().getStringArray(R.array.navigation_drawer_default_items);
    }


    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;

        if (position <= 5) return TYPE_CATEGORY;

        if (position == 6) return TYPE_HEADER_PLAYLIST;

        return TYPE_PLAYLIST;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        switch(type) {
            case TYPE_HEADER:
                break;
            case TYPE_CATEGORY:
                getViewTypeOne(position, convertView, parent);
                break;
            case TYPE_HEADER_PLAYLIST:
                break;
            case TYPE_PLAYLIST:
                getViewTypeTwo(position, convertView, parent);
                break;
        }

        return null;
    }


    private View getViewHeader(int position, View convertView, ViewGroup parent){
        ViewHolderHeader holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_header_1, null);
            holder = new ViewHolderHeader();
            holder.txtName = (TextView) row.findViewById(R.id.name);
            holder.txtMail = (TextView) row.findViewById(R.id.mail);
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imageView);
        }
        else {
            holder = (ViewHolderHeader) row.getTag();
        }

        // assign value to view


        return row;
    }

    private View getViewPlaylistHeader(int position, View convertView, ViewGroup parent) {
        ViewHolderPlaylistHeader holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_header_2, null);
            holder = new ViewHolderPlaylistHeader();
            holder.txtHeader = (TextView) row.findViewById(R.id.playlist_header);
        }
        else {
            holder = (ViewHolderPlaylistHeader) row.getTag();
        }

        // assign value to view

        return row;
    }

    private View getViewTypeOne(int position, View convertView, ViewGroup parent) {
        ViewHolderTypeOne holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_navigation_drawer_1, null);
            holder = new ViewHolderTypeOne();
            holder.txtView = (TextView) row.findViewById(R.id.text);
            holder.imageView = (ImageView) row.findViewById(R.id.icon);
        }
        else {
            holder = (ViewHolderTypeOne) row.getTag();
        }

        // assign value to holder
        holder.txtView.setText(categories[position]);
        switch(position) {
            case 0:
                // Trang chu
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 1:
                // Playlist cua toi
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 2:
                // Yeu Thich
                holder.imageView.setImageResource(R.drawable.ic_action_not_important);
                break;
            case 3:
                // Tim Theo Hop Am
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 4:
                // Tra cuu hop am
                holder.imageView.setImageResource(R.drawable.ic_action_settings);
                break;
        }

        return row;
    }

    private View getViewTypeTwo(int position, View convertView, ViewGroup parent) {
        ViewHolderTypeTwo holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_navigation_drawer_2, null);
            holder = new ViewHolderTypeTwo();
            holder.txtTitle = (TextView) row.findViewById(R.id.title);
            holder.txtDescription = (TextView) row.findViewById(R.id.description);
            holder.txtNumberOfSong = (TextView) row.findViewById(R.id.countSongText);
        }
        else {
            holder = (ViewHolderTypeTwo) row.getTag();
        }

        // assign value to view

        return row;
    }



    private class ViewHolderTypeOne {
        ImageView imageView;
        TextView txtView;
    }

    private class ViewHolderTypeTwo {
        TextView txtTitle;
        TextView txtDescription;
        TextView txtNumberOfSong;
    }

    private class ViewHolderHeader {
        TextView txtName;
        TextView txtMail;
        ImageView imgAvatar;
    }

    private class ViewHolderPlaylistHeader {
        TextView txtHeader;
    }
}
