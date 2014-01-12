package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.view.BunnyApplication;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Use this Adapter both for Singer and Author
 * Created by ThaoHQSE60963 on 1/8/14.
 */
public class ArtistAdapter extends BaseAdapter implements InfinityListView.IInfinityAdapter {

    public static String TAG = makeLogTag(ArtistAdapter.class);

    Context mContext;

    public List<Artist> artists;

    public ArtistAdapter(Context context) {
        this.mContext = context;
        artists = new ArrayList<Artist>();
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    @Override
    public void addItem(Object obj) {
        artists.add((Artist)obj);
    }

    @Override
    public ArrayList returnItems() {
        return (ArrayList)artists;
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_artist, null);
            holder = new ViewHolder();
            holder.artistNameTxt = (TextView) row.findViewById(R.id.artist_name_txt);
            holder.tempTxt = (TextView) row.findViewById(R.id.temp_txt);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Artist s = artists.get(position);
        holder.artistNameTxt.setText(s.artistName);

        holder.tempTxt.setText(s.getNumOfSongs(BunnyApplication.mContext) + " bài");

        return row;
    }

    private static class ViewHolder {
        TextView artistNameTxt;
        TextView tempTxt;
    }
}
