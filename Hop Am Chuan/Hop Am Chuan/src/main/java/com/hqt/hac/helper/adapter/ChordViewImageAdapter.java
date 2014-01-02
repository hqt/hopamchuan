package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hac_library.helper.DrawHelper;
import com.hqt.hac.config.Config;
import com.hqt.hac.view.R;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;


public class ChordViewImageAdapter extends ChordViewAdapter {

    public static String TAG = makeLogTag(ChordViewAdapter.class);

    public ChordViewImageAdapter(Context mContext, String[] chords) {
        super(mContext, chords);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_chord_view_image, null);
            holder = new ViewHolder();
            holder.imageChord = (ImageView) row.findViewById(R.id.chord_image_view);
            holder.upButton = (ImageView) row.findViewById(R.id.up_button);
            holder.downButton = (ImageView) row.findViewById(R.id.down_button);
            holder.signTextView = (TextView) row.findViewById(R.id.text_view);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        // set data
        holder.imageChord.setImageDrawable(DrawHelper.getBitmapDrawable(mContext.getResources(), 100, 100, chords[position], index[position], 0));
        holder.signTextView.setText(index[position] + "");

        // set action
        final ViewHolder finalHolder = holder;
        holder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++index[position];
                index[position] = index[position] % Config.FRET_POSITION_PERIOD;
                finalHolder.signTextView.setText(index[position] + "");
                finalHolder.imageChord.setImageDrawable(DrawHelper.getBitmapDrawable(mContext.getResources(), 100, 100, chords[position], index[position], 0));
            }
        });

        holder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --index[position];
                if (index[position] < 0) index[position] = Config.FRET_POSITION_PERIOD;
                finalHolder.signTextView.setText(index[position] + "");
                finalHolder.imageChord.setImageDrawable(DrawHelper.getBitmapDrawable(mContext.getResources(), 100, 100, chords[position], index[position], 0));
            }
        });


        return row;
    }


    public static class ViewHolder {
        ImageView imageChord;
        ImageView upButton;
        ImageView downButton;
        TextView signTextView;
    }
}
