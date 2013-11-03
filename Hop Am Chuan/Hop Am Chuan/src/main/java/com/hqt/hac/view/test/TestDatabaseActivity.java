package com.hqt.hac.view.test;

import android.net.Uri;
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
import android.widget.TextView;

import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.ArtistDataAcessLayer;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.provider.HopAmChuanProvider;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.view.R;

import java.util.List;

public class TestDatabaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        TextView textView = (TextView) findViewById(R.id.textview);
        Uri uri = HopAmChuanDBContract.Artists.CONTENT_URI;
        String res = "";
        uri = Uri.withAppendedPath(uri, "singer/songs/" + 100 + "");

        res += uri.toString() + "\n";

        res += HopAmChuanProvider.buildExpandedSelection(uri).toString();

        List<Song> songs = ArtistDataAcessLayer.findAllSongsByArtist(getApplicationContext(), 5);

        for (Song song : songs) {
            res += song.toString() + "\n";
        }

        textView.setText(res);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_database, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_database_test, container, false);
            return rootView;
        }
    }
}
