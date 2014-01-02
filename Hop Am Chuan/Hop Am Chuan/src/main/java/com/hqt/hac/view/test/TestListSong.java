package com.hqt.hac.view.test;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.view.R;
import com.unittest.DatabaseTest;

import java.util.List;

public class TestListSong extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list_song);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


        // delete all database
        HopAmChuanDatabase.deleteDatabase(getApplicationContext());

        // create sample database
        DatabaseTest.prepareLocalDatabaseByHand(getApplicationContext());



        final TextView text = (TextView) findViewById(R.id.listSong);
        List<Song> songs = SongDataAccessLayer.getRandSongs(getApplicationContext(), 3);
        String a = "";
        for (Song song : songs) {
            a += song.toString() + "\n";
        }
        text.setText(a);

        final EditText editText = (EditText) findViewById(R.id.lastetSongIdSet);

        Button btn = (Button) findViewById(R.id.setLastestViewButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int songId = Integer.parseInt(editText.getText().toString());
                SongDataAccessLayer.setLastestView(getApplicationContext(), songId);


                List<Song> songs2 = SongDataAccessLayer.getRandSongs(getApplicationContext(), 4);
                String a = "";
                for (Song song : songs2) {
                    a += song.toString() + "\n";
                }
                text.setText(a);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_list_song, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent mActivity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
            View rootView = inflater.inflate(R.layout.fragment_test_list_song, container, false);
            return rootView;
        }
    }

}
