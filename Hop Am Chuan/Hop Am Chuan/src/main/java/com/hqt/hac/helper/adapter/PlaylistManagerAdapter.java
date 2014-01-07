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
import com.hqt.hac.view.fragment.PlaylistManagerFragment;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class PlaylistManagerAdapter extends BaseAdapter {

    private static final String TAG = makeLogTag(PlaylistManagerAdapter.class);
    Context mContext;
    public List<Playlist> playLists;

    public interface RightMenuClick {
        public void onRightMenuClick(View view, Playlist playlist);
    }

    public void setPlayLists(List<Playlist> playLists) {
        this.playLists = playLists;
    }

    public RightMenuClick rightMenuClick;

    public PlaylistManagerAdapter(Context context, List<Playlist> playlists) {
        this.mContext = context.getApplicationContext();
        this.playLists = playlists;
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
        ViewHolder holder = null;
        View row = convertView;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = inflater.inflate(R.layout.list_item_playlist, null);
            holder = new ViewHolder();
            holder.numberOfSongTxt = (TextView) row.findViewById(R.id.countSongText);
            holder.playListNameTxt = (TextView) row.findViewById(R.id.playlist);
            holder.descriptionTxt = (TextView) row.findViewById(R.id.description);
            holder.optionBtn = (ImageView) row.findViewById(R.id.imageOptionView);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final Playlist rowItem = (Playlist) getItem(position);
        holder.numberOfSongTxt.setText(rowItem.numberOfSongs + ""); // carefully when set text is number
        holder.playListNameTxt.setText(rowItem.playlistName);
        holder.descriptionTxt.setText(rowItem.playlistDescription);

        final ViewHolder finalHolder = holder;
        holder.optionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x = finalHolder.optionBtn.getLeft();
                int y = finalHolder.optionBtn.getBottom();
                LOGE(TAG, "FFF Location On Screen Of View: " + x + "\t" + y);
                rightMenuClick.onRightMenuClick(view, rowItem);
            }
        });
        return row;
    }

    private class ViewHolder {
        TextView numberOfSongTxt;
        TextView playListNameTxt;
        TextView descriptionTxt;
        ImageView optionBtn;
    }

    public static interface IPlaylistManagerAdapter {
        public void sharePlaylist();
        public void renamePlaylist();
        public void deletePlaylist();
    }
}
