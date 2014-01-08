package com.hqt.hac.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.IContextMenu;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.helper.widget.InfinityListView;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.ArtistDataAccessLayer;
import com.hqt.hac.model.dal.SongArtistDataAccessLayer;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.view.fragment.SongDetailFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;

public class ArtistViewActivity extends ActionBarActivity implements InfinityListView.ILoaderContent {

    List<Song> songs;
    SongListAdapter songlistAdapter;
    private InfinityListView mListView;
    private PopupWindow popupWindow;
    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_view);

        // TODO: load artist here
        artist = ArtistDataAccessLayer.getArtistByName(getApplicationContext(), "N/A");


        songs = new ArrayList<Song>();
        songlistAdapter = new SongListAdapter(this, songs);

        /** ListView Configure */
        mListView = (InfinityListView) findViewById(R.id.listview);
        /** config mode for this ListView.
         *  this ListView is full rich function. See document for more detail
         */
        InfinityListView.ListViewProperty property = new InfinityListView.ListViewProperty();
        property.Loader(this).Adapter(songlistAdapter).FirstProcessLoading(true)
                .NumPerLoading(Config.DEFAULT_SONG_NUM_PER_LOAD).RunningBackground(true);
        mListView.setListViewProperty(property);
        mListView.resetListView(songlistAdapter);

        // Event for right menu click
        popupWindow = DialogUtils.createPopup(getLayoutInflater(), R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(this, popupWindow);

        // Event received from mAdapter.
        songlistAdapter.contextMenuDelegate = new IContextMenu() {
            @Override
            public void onMenuClick(View view, Song song, ImageView theStar) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, theStar);
            }
        };

        // Event for Item Click on ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                SongDetailFragment fragment = new SongDetailFragment();
//                Bundle arguments = new Bundle();
//                arguments.putParcelable("song", songs.get(position));
//                fragment.setArguments(arguments);
//                switchFragmentNormal(fragment);
//                changeTitleBar(songs.get(position).title);
                LOGE("TRUNGDQ", "song: " + songs.get(position));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.artist_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Collection load(int offset, int count) {
        return ArtistDataAccessLayer.searchSongByArtist(getApplicationContext(), artist.artistName, count);
    }

}
