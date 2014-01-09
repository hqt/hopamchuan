package com.hqt.hac.helper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.hqt.hac.model.Artist;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Use this Adapter both for Singer and Author
 * Created by ThaoHQSE60963 on 1/8/14.
 */
public class ArtistAdapter extends BaseAdapter {

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
        return null;
    }

    private static class Holder {

    }
}
