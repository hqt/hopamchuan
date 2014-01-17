package com.hac.android.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hac.android.config.Config;
import com.hac.android.utils.LogUtils;
import com.hac.chorddroid.components.ChordTextureView;
import com.hac.android.guitarchord.R;

public class ChordViewTextureAdapter extends ChordViewAdapter {
    public static String TAG = LogUtils.makeLogTag(ChordViewAdapter.class);

    public ChordViewTextureAdapter(Context mContext, String[] chords) {
        super(mContext, chords);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_chord_view_texture_view, null);
            holder = new ViewHolder();
            holder.imageChord = (ChordTextureView) row.findViewById(R.id.chord_texture_view);
            holder.upButton = (ImageView) row.findViewById(R.id.up_button);
            holder.downButton = (ImageView) row.findViewById(R.id.down_button);
            holder.signTextView = (TextView) row.findViewById(R.id.text_view);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        // set data
        if (holder.imageChord != null) holder.imageChord.drawChord(chords[position], index[position]);
        holder.signTextView.setText(index[position] + "");

        // set action
        final ViewHolder finalHolder = holder;
        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.imageChord.nextPosition();
                ++index[position];
                index[position] = index[position] % Config.FRET_POSITION_PERIOD;
                finalHolder.signTextView.setText(index[position] + "");
            }
        });

        holder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.imageChord.prevPosition();
                --index[position];
                if (index[position] < 0) index[position] = Config.FRET_POSITION_PERIOD;
                finalHolder.signTextView.setText(index[position] + "");
            }
        });


        return row;
    }

    public static class ViewHolder {
        ChordTextureView imageChord;
        ImageView upButton;
        ImageView downButton;
        TextView signTextView;
    }
}
