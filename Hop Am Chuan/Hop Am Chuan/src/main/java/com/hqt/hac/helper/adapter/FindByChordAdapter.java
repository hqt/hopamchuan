package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.view.R;
import com.hqt.hac.view.fragment.FindByChordFragment;

import java.util.List;

public class FindByChordAdapter extends ArrayAdapter {

    Context mContext;

    /** delegate is the callback to fragment / activity */
    IFindByChordAdapter delegate;

    public List<String> chords;

    private View.OnTouchListener mTouchListener;

    public FindByChordAdapter(Context context, IFindByChordAdapter delegate, List<String> chords) {
        super(context, R.layout.list_item_chord_search,chords);
        this.mContext = context.getApplicationContext();
        this.delegate = delegate;
        this.chords = chords;
    }

    public void setTouchListener(View.OnTouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
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
            row.setOnTouchListener(mTouchListener);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        holder.chordTextView.setText(chords.get(position));
        holder.removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegate.removeChordFromList(position);
            }
        });

        return row;
    }

    public static class ViewHolder {
        public TextView chordTextView;
        public ImageView removeImageView;
    }

    public static interface IFindByChordAdapter {
        public void removeChordFromList(int position);
    }
}
