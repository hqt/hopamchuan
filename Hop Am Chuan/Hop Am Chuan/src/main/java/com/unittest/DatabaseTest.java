package com.unittest;

import android.content.Context;

import com.hqt.hac.helper.Helper;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.ArtistDataAcessLayer;
import com.hqt.hac.model.dao.ChordDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.SongArtistDataAccessLayer;
import com.hqt.hac.model.dao.SongChordDataAccessLayer;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.utils.ParserUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Quang Trung on 11/4/13.
 */
public class DatabaseTest {
    /**
     * Use this method for easier development phrase
     */
    public static void prepareLocalDatabase(Context context) {
        // create song database
        List<Song> songs = ParserUtils.getAllSongsFromResource(context);
        SongDataAccessLayer.insertListOfSongs(context, songs);

        // create artist database
        List<Artist> artists = ParserUtils.getAllArtistsFromRescource(context);
        ArtistDataAcessLayer.insertListOfArtists(context, artists);

        // create chord database
        List<Chord> chords = ParserUtils.getAllChordsFromResource(context);
        ChordDataAccessLayer.insertListOfChords(context, chords);
    }


    public static void prepareLocalDatabaseByHand(Context context) {
        // create three artist
        Artist a1 = new Artist(1, "Huynh Quang Thao", "Huynh Quang Thao");
        Artist a2 = new Artist(2, "Dinh Quang Trung", "Dinh Quang Tring");
        Artist a3 = new Artist(3, "Pham Thi Thu Hoa", "Pham Thi Thu Hoa");
        ArtistDataAcessLayer.insertArtist(context, a1);
        ArtistDataAcessLayer.insertArtist(context, a2);
        ArtistDataAcessLayer.insertArtist(context, a3);

        // create two songs
        Song s1 = new Song(1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
        Song s2 = new Song(2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
        Song s3 = new Song(3, "Quoc Ca", "www.echip.com.vn", "doan quan Viet Nam di", "doan quan Viet Nam", new Date());
        SongDataAccessLayer.insertSong(context, s1);
        SongDataAccessLayer.insertSong(context, s2);
        SongDataAccessLayer.insertSong(context, s3);

        // create author
        // thao : author of two songs
        SongArtistDataAccessLayer.insertSong_Author(context, 1, 1);
        SongArtistDataAccessLayer.insertSong_Author(context, 2, 1);

        // create singer
        //  Trung and Hoa sing Chau Len Ba (1)
        // Hoa sings Quoc Ca (3)
        SongArtistDataAccessLayer.insertSong_Singer(context, 1, 1);
        SongArtistDataAccessLayer.insertSong_Singer(context, 1, 3);
        SongArtistDataAccessLayer.insertSong_Singer(context, 3, 3);

        /*// create favorite includes two songs
        FavoriteDataAccessLayer.addSongToFavorite(context, 1);
        FavoriteDataAccessLayer.addSongToFavorite(context, 2);*/

        // Create sample playlists
        Playlist playlist1 = new Playlist(1, "Playlist 1", "Mot", new Date(), 1);
        Playlist playlist2 = new Playlist(2, "Playlist 2", "Hai", new Date(), 0);
        Playlist playlist3 = new Playlist(3, "Playlist 3", "Ba", new Date(), 1);
        PlaylistDataAccessLayer.addNewPlaylist(context, playlist1);
        PlaylistDataAccessLayer.addNewPlaylist(context, playlist2);
        PlaylistDataAccessLayer.addNewPlaylist(context, playlist3);

        PlaylistDataAccessLayer.removePlaylistById(context, 2);
    }

    public static String TestInsertSong_Chord(Context context) {
        String res = "TestInsertSong_Chord: ";
        try {
            SongChordDataAccessLayer.insertSong_Chord(context, 1, 1);
            res += "N/A";
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }
        SongChordDataAccessLayer.removeSong_Chord(context, 1, 1);
        return res;
    }

    /**
     * This require SongDataAccessLayer.insertSong to be worked correctly
     *
     * @param context
     * @return
     */
    public static String TestGetSongById(Context context) {
        String res = "TestGetSongById: ";
        try {
            // Create
            Artist a1 = new Artist(1, "Huynh Quang Thao", "Huynh Quang Thao");
            Artist a2 = new Artist(2, "Dinh Quang Trung", "Dinh Quang Trung");
            List<Artist> inputA = new ArrayList<Artist>();
            inputA.add(a1);
            inputA.add(a2);

            Artist s1 = new Artist(3, "Singer 1", "Singer 1");
            Artist s2 = new Artist(4, "Singer 2", "Singer 2");
            List<Artist> inputS = new ArrayList<Artist>();
            inputS.add(s1);
            inputS.add(s2);

            Chord c1 = new Chord(1, "Am");
            Chord c2 = new Chord(2, "E");
            List<Chord> inputC = new ArrayList<Chord>();
            inputC.add(c1);
            inputC.add(c2);

            Song song = new Song(4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertSong(context, song);

            ArtistDataAcessLayer.insertListOfArtists(context, inputA);
            ArtistDataAcessLayer.insertListOfArtists(context, inputS);

            ChordDataAccessLayer.insertListOfChords(context, inputC);

            SongArtistDataAccessLayer.insertSong_Author(context, 4, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 4, 2);

            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 3);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 4);

            SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 4, 2);


            // Get
            Song result = SongDataAccessLayer.getSongById(context, 4);

            // Compare
            if (result.equals(song)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " expected: " + song.toString();
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);

        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 3);
        ArtistDataAcessLayer.removeArtistByid(context, 4);

        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);

        SongArtistDataAccessLayer.removeSong_Author(context, 4, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 4, 2);

        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 3);
        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 4);

        SongChordDataAccessLayer.removeSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 4, 2);
        return res;
    }

    public static String TestGetAuthorsBySongId(Context context) {
        String res = "TestGetAuthorsBySongId: ";
        try {
            // Create
            Song s2 = new Song(2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
            Artist a1 = new Artist(1, "Huynh Quang Thao", "Huynh Quang Thao");
            Artist a2 = new Artist(2, "Dinh Quang Trung", "Dinh Quang Trung");

            List<Artist> input = new ArrayList<Artist>();
            input.add(a1);
            input.add(a2);

            // Insert (set Trung & Thao is the authors of Lang toi)
            SongDataAccessLayer.insertSong(context, s2);
            ArtistDataAcessLayer.insertArtist(context, a1);
            ArtistDataAcessLayer.insertArtist(context, a2);
            SongArtistDataAccessLayer.insertSong_Author(context, 2, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 2, 2);

            // Get
            List<Artist> authors = SongDataAccessLayer.getAuthorsBySongId(context, 2);

            // Compare
            if (authors.size() == 2 && authors.get(0).equals(a1) && authors.get(1).equals(a2)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + Helper.arrayToString(authors) + " Expected: " + Helper.arrayToString(input);
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        SongArtistDataAccessLayer.removeSong_Author(context, 2, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 2, 2);

        return res;
    }

    public static String TestGetSingersBySongId(Context context) {
        String res = "TestGetSingersBySongId: ";
        try {
            // Create
            Song s2 = new Song(2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
            Artist a1 = new Artist(1, "Huynh Quang Thao", "Huynh Quang Thao");
            Artist a2 = new Artist(2, "Dinh Quang Trung", "Dinh Quang Trung");

            List<Artist> input = new ArrayList<Artist>();
            input.add(a1);
            input.add(a2);

            // Insert (set Trung & Thao is the singers of Lang toi)
            SongDataAccessLayer.insertSong(context, s2);
            ArtistDataAcessLayer.insertArtist(context, a1);
            ArtistDataAcessLayer.insertArtist(context, a2);
            SongArtistDataAccessLayer.insertSong_Singer(context, 2, 1);
            SongArtistDataAccessLayer.insertSong_Singer(context, 2, 2);

            // Get
            List<Artist> singers = SongDataAccessLayer.getSingersBySongId(context, 2);

            // Compare
            if (singers.size() == 2 && singers.get(0).equals(a1) && singers.get(1).equals(a2)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + Helper.arrayToString(singers) + " Expected: " + Helper.arrayToString(input);
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        SongArtistDataAccessLayer.removeSong_Singer(context, 2, 1);
        SongArtistDataAccessLayer.removeSong_Singer(context, 2, 2);
        return res;
    }

    public static String TestGetChordsBySongId(Context context) {
        String res = "TestGetChordsBySongId: ";
        try {
            // Create
            Chord c1 = new Chord(1, "Am");
            Chord c2 = new Chord(2, "E");
            List<Chord> input = new ArrayList<Chord>();
            input.add(c1);
            input.add(c2);

            Song s3 = new Song(3, "Quoc Ca", "www.echip.com.vn", "doan quan Viet Nam di", "doan quan Viet Nam", new Date());

            // Insert (set Am & E is the chords of Quoc Ca)
            SongDataAccessLayer.insertSong(context, s3);
            ChordDataAccessLayer.insertChord(context, c1);
            ChordDataAccessLayer.insertChord(context, c2);
            SongChordDataAccessLayer.insertSong_Chord(context, 3, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 3, 2);

            // Get
            List<Chord> result = SongDataAccessLayer.getChordsBySongId(context, 3);

            // Compare
            if (result.size() == 2 && result.get(0).equals(c1) && result.get(1).equals(c2)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + Helper.arrayToString(result) + " Expected: " + Helper.arrayToString(input);
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 3);
        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);
        SongChordDataAccessLayer.removeSong_Chord(context, 3, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 3, 2);
        return res;
    }


}
