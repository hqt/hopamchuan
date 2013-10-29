package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.view.R;

public class NavigationDrawerAdapter extends BaseAdapter {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {

        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        return null;

    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtView;
    }



}
