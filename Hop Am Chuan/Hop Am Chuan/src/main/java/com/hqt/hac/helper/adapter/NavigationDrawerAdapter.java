package com.hqt.hac.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.utils.EncodingUtils;
import com.hqt.hac.view.LoginActivity;
import com.hqt.hac.view.R;
import com.hqt.hac.view.popup.ProfilePopup;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class NavigationDrawerAdapter {

    private static String TAG = makeLogTag(NavigationDrawerAdapter.class);


    public static class HeaderAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(HeaderAdapter.class);

        Context mContext;
        Activity activity;
        IHeaderDelegate delegate;

        public HeaderAdapter(Activity activity) {
            this.activity = activity;
            this.mContext = activity.getApplicationContext();
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

            holder.txtName.setText(PrefStore.getLoginUsername(mContext));
            holder.txtMail.setText(PrefStore.getEmail(mContext));

            // Makes the marquee running
            holder.txtMail.setSelected(true);
            holder.txtName.setSelected(true);

            Bitmap imageAvatar = EncodingUtils.decodeByteToBitmap(PrefStore.getUserImage(mContext));
            if (imageAvatar != null) {
                holder.imgAvatar.setImageBitmap(imageAvatar);
            } else {
                holder.imgAvatar.setImageResource(R.drawable.default_avatar);
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TrungDQ: if you user has logged in, then display Logout popup
                    Bitmap checkLoggedIn = EncodingUtils.decodeByteToBitmap(PrefStore.getUserImage(mContext));
                    if (checkLoggedIn == null) {
                        // start Login Activity
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                        activity.finish();

                        // TrungDQ: Prefer popup than an mActivity
//                        LoginPopup loginPopup = new LoginPopup(mActivity);
//                        loginPopup.show();
                    } else {
                        // Start logout mActivity or popup here.
                        ProfilePopup profilePopup = new ProfilePopup(activity);
                        profilePopup.show();
                    }

                }
            });

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
            SONGS,
            MYPLAYLIST,
            FAVORITE,
            FIND_BY_CHORD,
            SEARCH_CHORD,
            SETTING
        }

        Context mContext;
        String[] categories;
        IItemDelegate mDelegate;

        public ItemAdapter(Context context) {
            this.mContext = context.getApplicationContext();
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
                    // Bài hát
                    holder.imageView.setImageResource(R.drawable.songs_icon);
                    type = TYPE.SONGS;
                    break;
                case 2:
                    // Playlist cua toi
                    holder.imageView.setImageResource(R.drawable.playlist_icon);
                    type = TYPE.MYPLAYLIST;
                    break;
                case 3:
                    // Yeu Thich
                    holder.imageView.setImageResource(R.drawable.favorite_icon);
                    type = TYPE.FAVORITE;
                    break;
                case 4:
                    // Tim Theo Hop Am
                    holder.imageView.setImageResource(R.drawable.search_icon);
                    type = TYPE.FIND_BY_CHORD;
                    break;
                case 5:
                    // Tra cuu hop am
                    holder.imageView.setImageResource(R.drawable.chord_icon);
                    type = TYPE.SEARCH_CHORD;
                    break;
                case 6:
                    // Cai dat
                    holder.imageView.setImageResource(R.drawable.setting_icon);
                    type = TYPE.SETTING;
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
            this.mContext = context.getApplicationContext();
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
            this.mContext = context.getApplicationContext();
            // load all playlist
            playlists = PlaylistDataAccessLayer.getAllPlayLists(context);
        }

        /** use this constructor for performance */
        public PlaylistItemAdapter(Context context, List<Playlist> playlists) {
            this.mContext = context.getApplicationContext();
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

