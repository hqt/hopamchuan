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

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class NavigationDrawerAdapter {

    private static String TAG = makeLogTag(NavigationDrawerAdapter.class);

    public static class HeaderAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(HeaderAdapter.class);

        Context mContext;

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
            }
            else {
                holder = (ViewHolderHeader) row.getTag();
            }

            // assign value to view
            holder.txtName.setText("huynh quang thao");
            holder.txtMail.setText("huynhquangthao@gmail.com");
            holder.imgAvatar.setImageResource(R.drawable.ic_menu_search);
            return row;
        }
    }

    public static class ItemAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(ItemAdapter.class);

        Context mContext;
        String[] categories;

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
            ViewHolderItemTypeOne holder = null;
            View row = convertView;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (row == null) {
                row = inflater.inflate(R.layout.list_item_navigation_drawer_1, null);
                holder = new ViewHolderItemTypeOne();
                holder.txtView = (TextView) row.findViewById(R.id.text);
                holder.imageView = (ImageView) row.findViewById(R.id.icon);
            }
            else {
                holder = (ViewHolderItemTypeOne) row.getTag();
            }

            // assign value to holder
            holder.txtView.setText(categories[position]);
            switch(position) {
                case 0:
                    // Trang chu
                    holder.imageView.setImageResource(R.drawable.ic_menu_search);
                    break;
                case 1:
                    // Playlist cua toi
                    holder.imageView.setImageResource(R.drawable.ic_menu_search);
                    break;
                case 2:
                    // Yeu Thich
                    holder.imageView.setImageResource(R.drawable.ic_action_not_important);
                    break;
                case 3:
                    // Tim Theo Hop Am
                    holder.imageView.setImageResource(R.drawable.ic_menu_search);
                    break;
                case 4:
                    // Tra cuu hop am
                    holder.imageView.setImageResource(R.drawable.ic_action_settings);
                    break;
            }
            return row;

        }
    }

    public static class PlaylistHeaderAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(PlaylistHeaderAdapter.class);

        private Context mContext;

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
            }
            else {
                holder = (ViewHolderPlaylistHeader) row.getTag();
            }

            // assign value to view
            holder.txtHeader.setText("PLAYLIST CUA TOI");

            return row;
        }
    }

    public static class PlaylistItemAdapter extends BaseAdapter {

        private static String TAG = makeLogTag(PlaylistItemAdapter.class);

        private Context mContext;

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItemTypeTwo holder = null;
            View row = convertView;

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (row == null) {
                row = inflater.inflate(R.layout.list_item_navigation_drawer_2, null);
                holder = new ViewHolderItemTypeTwo();
                holder.txtTitle = (TextView) row.findViewById(R.id.title);
                holder.txtDescription = (TextView) row.findViewById(R.id.description);
                holder.txtNumberOfSong = (TextView) row.findViewById(R.id.countSongText);
            }
            else {
                holder = (ViewHolderItemTypeTwo) row.getTag();
            }

            // assign value to view

            return row;
        }
    }






























    private static class ViewHolderItemTypeOne {
        ImageView imageView;
        TextView txtView;
    }

    private static class ViewHolderItemTypeTwo {
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


}
