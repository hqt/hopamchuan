package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class NavigationDrawerAdapter {

    private static String TAG = makeLogTag(NavigationDrawerAdapter.class);


    public static class HeaderAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(HeaderAdapter.class);

        Context mContext;
        IHeaderDelegate delegate;

        public HeaderAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderHeader holder = null;
            View row = convertView;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (row == null) {
                row = inflater.inflate(R.layout.list_item_header_1, null);
                holder = new ViewHolderHeader();
                holder.txtName = (TextView) row.findViewById(R.id.name);
                holder.txtMail = (TextView) row.findViewById(R.id.mail);
                holder.imgAvatar = (ImageView) row.findViewById(R.id.imageView);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolderHeader) row.getTag();
            }

            // assign value to view
            holder.txtName.setText("HUỲNH QUANG THẢO");
            holder.txtMail.setText("huynhquangthao@gmail.com");
            holder.imgAvatar.setImageResource(R.drawable.default_avatar);
            return row;
        }

        public void setDelegate(IHeaderDelegate delegate) {
            this.delegate = delegate;
        }
    }

    public static class ItemAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(ItemAdapter.class);

        public enum TYPE {
            HOME,
            MYPLAYLIST,
            FAVORITE,
            FIND_BY_CHORD,
            SEARCH_CHORD
        }

        Context mContext;
        String[] categories;
        IItemDelegate mDelegate;

        public ItemAdapter(Context context) {
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
            ViewHolderItem holder = null;
            View row = convertView;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (row == null) {
                row = inflater.inflate(R.layout.list_item_navigation_drawer_1, null);
                holder = new ViewHolderItem();
                holder.txtView = (TextView) row.findViewById(R.id.text);
                holder.imageView = (ImageView) row.findViewById(R.id.icon);

                row.setTag(holder);
            }
            else {
                holder = (ViewHolderItem) row.getTag();
            }

            // assign value to holder
            TYPE type = null;
            try {
                holder.txtView.setText(categories[position]);
            }
            catch (Exception e) {
                if (holder == null) Log.e("Huynh Quang Thao", "Silly Error");
            }

            switch(position) {
                case 0:
                    // Trang chu
                    holder.imageView.setImageResource(R.drawable.home_icon);
                    type = TYPE.HOME;
                    break;
                case 1:
                    // Playlist cua toi
                    holder.imageView.setImageResource(R.drawable.playlist_icon);
                    type = TYPE.MYPLAYLIST;
                    break;
                case 2:
                    // Yeu Thich
                    holder.imageView.setImageResource(R.drawable.favorite_icon);
                    type = TYPE.FAVORITE;
                    break;
                case 3:
                    // Tim Theo Hop Am
                    holder.imageView.setImageResource(R.drawable.search_icon);
                    type = TYPE.FIND_BY_CHORD;
                    break;
                case 4:
                    // Tra cuu hop am
                    holder.imageView.setImageResource(R.drawable.chord_icon);
                    type = TYPE.SEARCH_CHORD;
                    break;
            }

            final TYPE finalType = type;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDelegate.gotoCategoryPage(finalType);
                }
            });
            return row;
        }

        public void setDelegate(IItemDelegate mDelegate) {
            this.mDelegate = mDelegate;
        }
    }

    public static class PlaylistHeaderAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(PlaylistHeaderAdapter.class);

        private Context mContext;
        IPlaylistHeaderDelegate delegate;

        public PlaylistHeaderAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderPlaylistHeader holder = null;
            View row = convertView;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (row == null) {
                row = inflater.inflate(R.layout.list_item_header_2, null);
                holder = new ViewHolderPlaylistHeader();
                holder.txtHeader = (TextView) row.findViewById(R.id.playlist_header);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolderPlaylistHeader) row.getTag();
            }

            // assign value to view
            holder.txtHeader.setText("PLAYLIST CỦA TÔI");

            return row;
        }

        public void setDelegate(IPlaylistHeaderDelegate delegate) {
            this.delegate = delegate;
        }
    }

    public static class PlaylistItemAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(PlaylistItemAdapter.class);

        private Context mContext;
        IPlaylistItemDelegate delegate;
        List<Playlist> playlists;

        public PlaylistItemAdapter(Context context) {
            this.mContext = context;
            // load all playlist
            playlists = PlaylistDataAccessLayer.getAllPlayLists(context);
        }

        /** use this constructor for performance */
        public PlaylistItemAdapter(Context context, List<Playlist> playlists) {
            this.mContext = context;
            this.playlists = playlists;
        }

        @Override
        public int getCount() {
            return playlists.size();
        }

        @Override
        public Object getItem(int position) {
            return playlists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolderPlaylistItem holder = null;
            View row = convertView;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (row == null) {
                row = inflater.inflate(R.layout.list_item_navigation_drawer_2, null);
                holder = new ViewHolderPlaylistItem();
                holder.txtTitle = (TextView) row.findViewById(R.id.title);
                holder.txtDescription = (TextView) row.findViewById(R.id.description);
                holder.txtNumberOfSong = (TextView) row.findViewById(R.id.countSongText);
                row.setTag(holder);
            }
            else {
                holder = (ViewHolderPlaylistItem) row.getTag();
            }

            // assign value to view
            Playlist p = playlists.get(position);
            holder.txtTitle.setText(p.playlistName);
            holder.txtDescription.setText(p.playlistDescription);
            holder.txtNumberOfSong.setText(p.numberOfSongs + "");

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delegate.gotoPlayList(position);
                }
            });
            return row;
        }

        public void setDelegate(IPlaylistItemDelegate delegate) {
            this.delegate = delegate;
        }
    }

    private static class ViewHolderItem {
        ImageView imageView;
        TextView txtView;
    }

    private static class ViewHolderPlaylistItem {
        TextView txtTitle;
        TextView txtDescription;
        TextView txtNumberOfSong;
    }

    private static class ViewHolderHeader {
        TextView txtName;
        TextView txtMail;
        ImageView imgAvatar;
    }

    private static class ViewHolderPlaylistHeader {
        TextView txtHeader;
    }

    /**
     * Interface acts as Callback
     * for NavigationDrawer decide actions
     * this stimulate Delegate Design Pattern often use in C#
     * @author Huynh Quang Thao
     */

    public static interface IHeaderDelegate {

    }

    public static interface IItemDelegate {
        void gotoCategoryPage(ItemAdapter.TYPE type);

    }

    public static interface IPlaylistHeaderDelegate {

    }

    public static interface IPlaylistItemDelegate {
        void gotoPlayList(int playlistId);

    }


}

