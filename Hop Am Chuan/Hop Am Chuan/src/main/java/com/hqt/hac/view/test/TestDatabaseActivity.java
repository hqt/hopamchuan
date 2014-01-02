package com.hqt.hac.view.test;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.view.R;

public class TestDatabaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        TextView textView = (TextView) findViewById(R.id.textview);

        // delete database for consistency
//        HopAmChuanDatabase.deleteDatabase(getApplicationContext());

        String res = "";

//        // test 1 : Get Songs By Author
//        res += "All Songs By Author Huynh Quang Thao (should be 2):\n";
//        List<Song> songs = ArtistDataAccessLayer.findAllSongsByAuthor(getApplicationContext(), 1);
//        res += String.format("Size of Songs: %d\n", songs.size());
//        res += Helper.arrayToString(songs) + "\n";
//
//        // test 2 : Get Songs By Singer
//        res += "All Songs By Singer Pham Thi Thu Hoa (should be 2) and different from above:\n";
//        songs = ArtistDataAccessLayer.findAllSongsBySinger(getApplicationContext(), 3);
//        res += Helper.arrayToString(songs) + "\n";
//
//        // test 3 : test get artist by id
//        Artist artist = ArtistDataAccessLayer.getArtistById(getApplicationContext(), 2);
//        res += artist + "\n";
//
//        List<Playlist> playlists = PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());
//        res += Helper.arrayToString(playlists);

//        res += DatabaseTest.TestGetArtistById(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestInsertSong_Chord(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetAuthorsBySongId(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetSingersBySongId(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetChordsBySongId(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetSongById(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestFindAllSongsByAuthor(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestFindAllSongsBySinger(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetAllFavoriteSongs(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestInFavorite(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetPlaylistById(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetAllSongsFromPlaylist(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestRemovePlaylistSong(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestRenamePlaylist(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetChordByName(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetRandomSongsByAuthor(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetRandomSongsBySinger(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestInsertFullSongSync(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestSearchSongByTitle(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestGetArtistByName(getApplicationContext()) + "\n";
//        res += DatabaseTest.TestSearchSongByArtist(getApplicationContext()) + "\n";
        res += PlaylistDataAccessLayer.getMaxPlaylistId(getApplicationContext()) + "\n";

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
        // as you specify a parent mActivity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}