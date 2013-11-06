package com.hqt.hac.view.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.utils.ParserUtils;
import com.hqt.hac.model.Song;
import com.hqt.hac.view.R;

import java.util.List;

public class TestParser extends ActionBarActivity {

    ListView listView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        listView = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.textview);

        //// Testing database

//        List<Artist> arr = ParserUtils.getAllArtistsFromRescource(getApplicationContext());
//
//        String uri = ArtistDataAcessLayer.insertArtist(getApplicationContext(), arr.get(0));

        List<Song> songs = ParserUtils.getAllSongsFromResource(getApplicationContext());
//        SongDataAccessLayer.insertFullSongListSync(getApplicationContext(), songs);
        SongDataAccessLayer.insertFullSongSync(getApplicationContext(), songs.get(1));
        Song song = SongDataAccessLayer.getSongById(getApplicationContext(), 2);
        textView.setText(song.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_testing, container, false);
            return rootView;
        }
    }

}
