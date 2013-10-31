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
import com.hqt.hac.view.R;

import static com.hqt.hac.Utils.LogUtils.makeLogTag;

public class NavigationDrawerAdapter extends BaseAdapter {

    private static final String TAG = makeLogTag(NavigationDrawerAdapter.class);

    Context mContext;
    String[] categories;

    int DEFAULT_SIZE = 0;

    public NavigationDrawerAdapter(Context context) {
        this.mContext = context;
        categories = context.getResources().getStringArray(R.array.navigation_drawer_default_items);
        DEFAULT_SIZE = categories.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position < DEFAULT_SIZE) return getViewTypeOne(position, convertView, parent);
        else return getViewTypeTwo(position, convertView, parent);
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
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 3:
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
                break;
            case 4:
                holder.imageView.setImageResource(R.drawable.ic_menu_search);
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
}
