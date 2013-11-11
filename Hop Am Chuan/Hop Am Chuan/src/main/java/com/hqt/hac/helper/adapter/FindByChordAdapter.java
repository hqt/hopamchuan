package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.view.R;

import java.util.List;

public class FindByChordAdapter extends BaseAdapter {

    Context mContext;

    List<String> chords;

    public FindByChordAdapter(Context context, List<String> chords) {
        this.mContext = context;
        this.chords = chords;
    }

    @Override
    public int getCount() {
        return chords.size();
    }

    @Override
    public Object getItem(int position) {
        return chords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_chord_search, null);
            holder = new ViewHolder();
            holder.chordTextView = (TextView) row.findViewById(R.id.text_view);
            holder.removeImageView = (ImageView) row.findViewById(R.id.image_view);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        holder.chordTextView.setText(chords.get(position));
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove current position of chords
                chords.remove(position);
                // refresh ListView
                notifyDataSetChanged();
            }
        });

        return row;
    }

    public static class ViewHolder {
        public TextView chordTextView;
        public ImageView removeImageView;
    }
}
