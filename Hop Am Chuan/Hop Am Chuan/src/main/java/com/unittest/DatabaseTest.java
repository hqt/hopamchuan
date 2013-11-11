package com.unittest;

import android.content.Context;
import android.util.Log;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.Helper;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.ArtistDataAcessLayer;
import com.hqt.hac.model.dao.ChordDataAccessLayer;
import com.hqt.hac.model.dao.FavoriteDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistSongDataAccessLayer;
import com.hqt.hac.model.dao.SongArtistDataAccessLayer;
import com.hqt.hac.model.dao.SongChordDataAccessLayer;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.utils.ParserUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


        // create songs
        Song s1 = new Song(Config.DEFAULT_SONG_ID, 1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
        Song s2 = new Song(Config.DEFAULT_SONG_ID, 2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
        Song s3 = new Song(Config.DEFAULT_SONG_ID, 3, "Quoc Ca", "www.echip.com.vn", "doan quan Viet Nam di", "doan quan Viet Nam", new Date());
        Song s4 = new Song(Config.DEFAULT_SONG_ID, 4, "Dem Dong", "www.echip.com.vn", "nguoi co lu dem dong khong nha", "dem dong khong nha", new Date());
        Song s5 = new Song(Config.DEFAULT_SONG_ID, 5, "Suoi mo", "www.echip.com.vn", "dong nuoc troi lung lo ngoai nang", "dong nuoc troi", new Date());

        SongDataAccessLayer.insertSong(context, s1);
        SongDataAccessLayer.insertSong(context, s2);
        SongDataAccessLayer.insertSong(context, s3);
        SongDataAccessLayer.insertSong(context, s4);
        SongDataAccessLayer.insertSong(context, s5);

        // create author
        // thao : author of two songs
        SongArtistDataAccessLayer.insertSong_Author(context, 1, 1);
        SongArtistDataAccessLayer.insertSong_Author(context, 2, 1);

        // create chords
        Chord c1 = new Chord(1, "Am");
        Chord c2 = new Chord(2, "E");
        Chord c3 = new Chord(3, "C");
        Chord c4 = new Chord(4, "G");
        ChordDataAccessLayer.insertChord(context, c1);
        ChordDataAccessLayer.insertChord(context, c2);
        ChordDataAccessLayer.insertChord(context, c3);
        ChordDataAccessLayer.insertChord(context, c4);

        // assign chords to songs
        // 1 "Quoc Ca" will be Am E C
        // 4 "Dem Dong" will be Am C G
        SongChordDataAccessLayer.insertSong_Chord(context, 1, 1);
        SongChordDataAccessLayer.insertSong_Chord(context, 1, 2);
        SongChordDataAccessLayer.insertSong_Chord(context, 1, 3);
        SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.insertSong_Chord(context, 4, 3);
        SongChordDataAccessLayer.insertSong_Chord(context, 4, 4);

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
        Playlist playlist1 = new Playlist(1, "Nhac Trinh Cong Son", "Trinh Cong Son", new Date(), 1);
        Playlist playlist2 = new Playlist(2, "Nhac Pham Duy", "Pham Duy", new Date(), 0);
        Playlist playlist3 = new Playlist(3, "Nhac Tien Chien", "Tien Chien", new Date(), 1);
        PlaylistDataAccessLayer.insertPlaylist(context, playlist1);
        PlaylistDataAccessLayer.insertPlaylist(context, playlist2);
        PlaylistDataAccessLayer.insertPlaylist(context, playlist3);

        // create songs in playlists
        PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 1);
        PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 2);
        PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 3);
        PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 2, 3);

        // create favorites
        FavoriteDataAccessLayer.addSongToFavorite(context, 1);
        FavoriteDataAccessLayer.addSongToFavorite(context, 2);
        FavoriteDataAccessLayer.addSongToFavorite(context, 3);

    }

    public static String TestInsertSong_Chord(Context context) {
        String res = "TestInsertSong_Chord: ";
        try {
            SongChordDataAccessLayer.insertSong_Chord(context, 1, 1);
            res += "OK";
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }
        SongChordDataAccessLayer.removeSong_Chord(context, 1, 1);
        return res;
    }

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

            Song song = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);


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

    public static String TestInsertFullSongSync(Context context) {
        String res = "TestInsertFullSongSync: ";
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

            Song song = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertFullSongSync(context, song);


            // Get
            Song result = SongDataAccessLayer.getSongById(context, 4);

            Log.i("SongInsertDebug", "Before get:" + result.toString());
            result.getAuthors(context);
            result.getSingers(context);
            result.getChords(context);
            result.getContent(context);
            Log.i("SongInsertDebug", "After get:" + result.toString());

            // Compare
            if (result.equals(song)
                    && a1.equals(result.getAuthors(context).get(0))
                    && a2.equals(result.getAuthors(context).get(1))
                    && s1.equals(result.getSingers(context).get(0))
                    && s2.equals(result.getSingers(context).get(1))
                    && c1.equals(result.getChords(context).get(0))
                    && c2.equals(result.getChords(context).get(1))) {
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
            Song s2 = new Song(Config.DEFAULT_SONG_ID, 2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
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
            Song s2 = new Song(Config.DEFAULT_SONG_ID, 2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
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

            Song s3 = new Song(Config.DEFAULT_SONG_ID, 3, "Quoc Ca", "www.echip.com.vn", "doan quan Viet Nam di", "doan quan Viet Nam", new Date());

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

    public static String TestGetArtistById(Context context) {
        String res = "TestGetArtistById: ";
        try {
            // Create
            Artist a1 = new Artist(1, "Đinh Quang Trung", "Dinh Quang Trung");

            // Insert (set Am & E is the chords of Quoc Ca)
            ArtistDataAcessLayer.insertArtist(context, a1);

            // Get
            Artist result = ArtistDataAcessLayer.getArtistById(context, 1);

            // Compare
            if (result != null && result.equals(a1)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " Expected: " + a1.toString();
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        ArtistDataAcessLayer.removeArtistByid(context, 1);
        return res;
    }

    public static String TestFindAllSongsByAuthor(Context context) {
        String res = "TestFindAllSongsByAuthor: ";
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

            Song song1 = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);
            Song song2 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Len nam", "www.55555.com", "chau len nam chau vo mau giao", "chau len nam", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertSong(context, song1);
            SongDataAccessLayer.insertSong(context, song2);

            ArtistDataAcessLayer.insertListOfArtists(context, inputA);
            ArtistDataAcessLayer.insertListOfArtists(context, inputS);

            ChordDataAccessLayer.insertListOfChords(context, inputC);

            SongArtistDataAccessLayer.insertSong_Author(context, 4, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 4, 2);
            SongArtistDataAccessLayer.insertSong_Author(context, 5, 2);

            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 3);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 4);
            SongArtistDataAccessLayer.insertSong_Singer(context, 5, 4);

            SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 4, 2);


            // Get
            // should be song 4 and 5
            List<Song> result = ArtistDataAcessLayer.findAllSongsByAuthor(context, 2);

            // Compare
            if (result.size() == 2 && result.get(0).equals(song1) && result.get(1).equals(song2)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " expected: " + song1.toString() + " | " + song2.toString();
                Log.i("DatabaseTestDebug", res);
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);
        SongDataAccessLayer.removeSongById(context, 5);

        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 3);
        ArtistDataAcessLayer.removeArtistByid(context, 4);

        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);

        SongArtistDataAccessLayer.removeSong_Author(context, 4, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 4, 2);
        SongArtistDataAccessLayer.removeSong_Author(context, 5, 2);

        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 3);
        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 4);
        SongArtistDataAccessLayer.removeSong_Singer(context, 5, 4);

        SongChordDataAccessLayer.removeSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 4, 2);
        return res;
    }

    public static String TestFindAllSongsBySinger(Context context) {
        String res = "TestFindAllSongsBySinger: ";
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

            Song song1 = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);
            Song song2 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Len nam", "www.55555.com", "chau len nam chau vo mau giao", "chau len nam", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertSong(context, song1);
            SongDataAccessLayer.insertSong(context, song2);

            ArtistDataAcessLayer.insertListOfArtists(context, inputA);
            ArtistDataAcessLayer.insertListOfArtists(context, inputS);

            ChordDataAccessLayer.insertListOfChords(context, inputC);

            SongArtistDataAccessLayer.insertSong_Author(context, 4, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 4, 2);
            SongArtistDataAccessLayer.insertSong_Author(context, 5, 2);

            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 3);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 4);
            SongArtistDataAccessLayer.insertSong_Singer(context, 5, 4);

            SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 4, 2);


            // Get
            // should be song 4 and 5
            List<Song> result = ArtistDataAcessLayer.findAllSongsBySinger(context, 4);

            // Compare
            if (result.size() == 2 && result.get(0).equals(song1) && result.get(1).equals(song2)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " expected: " + song1.toString() + " | " + song2.toString();
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);
        SongDataAccessLayer.removeSongById(context, 5);

        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 3);
        ArtistDataAcessLayer.removeArtistByid(context, 4);

        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);

        SongArtistDataAccessLayer.removeSong_Author(context, 4, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 4, 2);
        SongArtistDataAccessLayer.removeSong_Author(context, 5, 2);

        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 3);
        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 4);
        SongArtistDataAccessLayer.removeSong_Singer(context, 5, 4);

        SongChordDataAccessLayer.removeSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 4, 2);
        return res;
    }

    public static String TestGetRandomSongsByAuthor(Context context) {
        String res = "TestGetRandomSongsByAuthor: ";
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

            Song song1 = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);
            Song song2 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Len nam", "www.55555.com", "chau len nam chau vo mau giao", "chau len nam", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertSong(context, song1);
            SongDataAccessLayer.insertSong(context, song2);

            ArtistDataAcessLayer.insertListOfArtists(context, inputA);
            ArtistDataAcessLayer.insertListOfArtists(context, inputS);

            ChordDataAccessLayer.insertListOfChords(context, inputC);

            SongArtistDataAccessLayer.insertSong_Author(context, 4, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 4, 2);
            SongArtistDataAccessLayer.insertSong_Author(context, 5, 2);

            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 3);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 4);
            SongArtistDataAccessLayer.insertSong_Singer(context, 5, 4);

            SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 4, 2);


            // Get
            for (int i = 0; i < 10; i++) {
                // Should be song 4 or 5 unpredictable
                List<Song> result = ArtistDataAcessLayer.getRandomSongsByAuthor(context, 2, 1);

                // Compare
                if (result.size() == 1 && result.get(0).equals(song1)) {
                    res += "X";
                } else if (result.size() == 1 && result.get(0).equals(song2)) {
                    res += "O";
                } else {
                    res += '-';
                }
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);
        SongDataAccessLayer.removeSongById(context, 5);

        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 3);
        ArtistDataAcessLayer.removeArtistByid(context, 4);

        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);

        SongArtistDataAccessLayer.removeSong_Author(context, 4, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 4, 2);
        SongArtistDataAccessLayer.removeSong_Author(context, 5, 2);

        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 3);
        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 4);
        SongArtistDataAccessLayer.removeSong_Singer(context, 5, 4);

        SongChordDataAccessLayer.removeSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 4, 2);
        return res;
    }

    public static String TestGetRandomSongsBySinger(Context context) {
        String res = "TestGetRandomSongsBySinger: ";
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

            Song song1 = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);
            Song song2 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Len nam", "www.55555.com", "chau len nam chau vo mau giao", "chau len nam", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertSong(context, song1);
            SongDataAccessLayer.insertSong(context, song2);

            ArtistDataAcessLayer.insertListOfArtists(context, inputA);
            ArtistDataAcessLayer.insertListOfArtists(context, inputS);

            ChordDataAccessLayer.insertListOfChords(context, inputC);

            SongArtistDataAccessLayer.insertSong_Author(context, 4, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 4, 2);
            SongArtistDataAccessLayer.insertSong_Author(context, 5, 2);

            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 3);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 4);
            SongArtistDataAccessLayer.insertSong_Singer(context, 5, 4);

            SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 4, 2);


            // Get
            for (int i = 0; i < 10; i++) {
                // Should be song 4 and 5 with unpredictable order
                List<Song> result = ArtistDataAcessLayer.getRandomSongsBySinger(context, 4, 1);

                // Compare
                if (result.size() == 1 && result.get(0).equals(song1)) {
                    res += "X";
                } else if (result.size() == 1 && result.get(0).equals(song2)) {
                    res += "O";
                } else {
                    res += '-';
                }
            }

        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);
        SongDataAccessLayer.removeSongById(context, 5);

        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 3);
        ArtistDataAcessLayer.removeArtistByid(context, 4);

        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);

        SongArtistDataAccessLayer.removeSong_Author(context, 4, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 4, 2);
        SongArtistDataAccessLayer.removeSong_Author(context, 5, 2);

        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 3);
        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 4);
        SongArtistDataAccessLayer.removeSong_Singer(context, 5, 4);

        SongChordDataAccessLayer.removeSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 4, 2);
        return res;
    }

    public static String TestGetAllFavoriteSongs(Context context) {
        String res = "TestGetAllFavoriteSongs: ";
        try {
            // Create
            Song s1 = new Song(Config.DEFAULT_SONG_ID, 1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());

            // Insert
            SongDataAccessLayer.insertSong(context, s1);
            FavoriteDataAccessLayer.addSongToFavorite(context, 1);

            // Get
            List<Song> songs = FavoriteDataAccessLayer.getAllFavoriteSongs(context);

            // Compare
            if (songs.size() == 1 && songs.get(0).equals(s1)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + Helper.arrayToString(songs) + " Expected: " + s1;
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        FavoriteDataAccessLayer.removeSongFromFavorite(context, 1);
        SongDataAccessLayer.removeSongById(context, 1);
        return res;
    }

    public static String TestInFavorite(Context context) {
        String res = "TestInFavorite: ";
        try {
            // Create
            Song s1 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
            Song s2 = new Song(Config.DEFAULT_SONG_ID, 7, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());

            // Insert
            SongDataAccessLayer.insertSong(context, s1);
            SongDataAccessLayer.insertSong(context, s2);
            FavoriteDataAccessLayer.addSongToFavorite(context, 7);

            // Get
            int result1 = FavoriteDataAccessLayer.isInFavorite(context, 5); // Should be 0
            int result2 = FavoriteDataAccessLayer.isInFavorite(context, 7); // Should be 7

            // Compare
            if (result1 == 0 && result2 == 7) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result1 + "|" + result2 + " Expected: " + 0 + "|7";
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        FavoriteDataAccessLayer.removeSongFromFavorite(context, 1);
        SongDataAccessLayer.removeSongById(context, 1);
        SongDataAccessLayer.removeSongById(context, 2);
        return res;
    }

    public static String TestGetPlaylistById(Context context) {
        String res = "TestGetPlaylistById: ";
        try {
            // Create
            Playlist playlist1 = new Playlist(1, "Playlist 1", "Mot", new Date(), 1);
            Song s1 = new Song(Config.DEFAULT_SONG_ID, 1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
            Song s2 = new Song(Config.DEFAULT_SONG_ID, 2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
            Song s3 = new Song(Config.DEFAULT_SONG_ID, 3, "Quoc Ca", "www.echip.com.vn", "doan quan Viet Nam di", "doan quan Viet Nam", new Date());

            // Insert
            SongDataAccessLayer.insertSong(context, s1);
            SongDataAccessLayer.insertSong(context, s2);
            SongDataAccessLayer.insertSong(context, s3);
            PlaylistDataAccessLayer.insertPlaylist(context, playlist1);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 1);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 2);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 3);

            // Get
            Playlist result = PlaylistDataAccessLayer.getPlaylistById(context, 1);

            // Compare
            if (result != null && result.equals(playlist1)) {
                res += " OK ";
            } else {
                res += " FAIL: result: " + result + " Expected: " + playlist1;
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        PlaylistDataAccessLayer.removePlaylistById(context, 1);
        SongDataAccessLayer.removeSongById(context, 3);
        SongDataAccessLayer.removeSongById(context, 2);
        SongDataAccessLayer.removeSongById(context, 1);
        PlaylistSongDataAccessLayer.removePlaylist_Song(context, 1);

        return res;
    }

    public static String TestGetAllSongsFromPlaylist(Context context) {
        String res = "TestGetAllSongsFromPlaylist: ";
        try {
            // Create
            Playlist playlist1 = new Playlist(1, "Playlist 1", "Mot", new Date(), 1);
            Song s1 = new Song(Config.DEFAULT_SONG_ID, 1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
            Song s2 = new Song(Config.DEFAULT_SONG_ID, 2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());

            // Insert
            SongDataAccessLayer.insertSong(context, s1);
            SongDataAccessLayer.insertSong(context, s2);
            PlaylistDataAccessLayer.insertPlaylist(context, playlist1);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 1);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 2);

            // Get
            List<Song> result = PlaylistDataAccessLayer.getAllSongsFromPlaylist(context, 1);

            // Compare
            if (result.size() == 2 && result.get(0).equals(s1) && result.get(1).equals(s2)) {
                res += " OK ";
            } else {
                res += " FAIL: result: " + result + " Expected: " + s1.toString() + "|" + s2.toString();
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        PlaylistDataAccessLayer.removePlaylistById(context, 1);
        SongDataAccessLayer.removeSongById(context, 2);
        SongDataAccessLayer.removeSongById(context, 1);
        PlaylistSongDataAccessLayer.removePlaylist_Song(context, 1);

        return res;
    }

    public static String TestRemovePlaylistSong(Context context) {
        String res = "TestRemovePlaylistSong: ";
        try {
            // Create
            Playlist playlist1 = new Playlist(1, "Playlist 1", "Mot", new Date(), 1);
            Song s1 = new Song(Config.DEFAULT_SONG_ID, 1, "Chau Len ba", "www.google.com", "chau len ba chau vo mau giao", "chau len ba", new Date());
            Song s2 = new Song(Config.DEFAULT_SONG_ID, 2, "Lang toi", "www.microsoft.com", "lang toi xanh bong tre", "lang toi", new Date());
            Song s3 = new Song(Config.DEFAULT_SONG_ID, 3, "Lang toi 3", "www.33333.com", "lang 333 xanh bong tre", "333 toi", new Date());

            // Insert
            SongDataAccessLayer.insertSong(context, s1);
            SongDataAccessLayer.insertSong(context, s2);
            SongDataAccessLayer.insertSong(context, s3);
            PlaylistDataAccessLayer.insertPlaylist(context, playlist1);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 1);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 2);
            PlaylistSongDataAccessLayer.insertPlaylist_Song(context, 1, 3);

            // Test 1
            // Get
            List<Song> result = PlaylistDataAccessLayer.getAllSongsFromPlaylist(context, 1);
            // Compare
            if (result.size() == 3 && result.get(0).equals(s1) && result.get(1).equals(s2) && result.get(2).equals(s3)) {
                res += " OK ";
            } else {
                res += " FAIL: result: " + result + " Expected: " + s1.toString() + "|" + s2.toString() + "|" + s3.toString();
            }


            // Test 2 Delete song 2 from playlist
            PlaylistSongDataAccessLayer.removePlaylist_Song(context, 1, 2);
            // Get
            List<Song> result2 = PlaylistDataAccessLayer.getAllSongsFromPlaylist(context, 1);
            // Compare
            if (result2.size() == 2 && result2.get(0).equals(s1) && result2.get(1).equals(s3)) {
                res += " OK ";
            } else {
                res += " FAIL: result: " + result2 + " Expected: " + s1.toString() + "|" + s3.toString();
            }

            // Test 3 Delete all song from playlist
            PlaylistSongDataAccessLayer.removePlaylist_Song(context, 1);
            // Get
            List<Song> result3 = PlaylistDataAccessLayer.getAllSongsFromPlaylist(context, 1);
            // Compare
            if (result3.size() == 0) {
                res += " OK ";
            } else {
                res += " FAIL: result: " + result3 + " Expected: {}";
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        PlaylistDataAccessLayer.removePlaylistById(context, 1);
        SongDataAccessLayer.removeSongById(context, 2);
        SongDataAccessLayer.removeSongById(context, 1);
        PlaylistSongDataAccessLayer.removePlaylist_Song(context, 1);

        return res;
    }

    public static String TestRenamePlaylist(Context context) {
        String res = "TestRenamePlaylist: ";
        try {
            // Create
            Playlist playlist1 = new Playlist(1, "Playlist 1", "Mot", new Date(), 1);

            // Insert
            PlaylistDataAccessLayer.insertPlaylist(context, playlist1);

            // Do rename & get
            PlaylistDataAccessLayer.renamePlaylist(context, 1, "New Name Playlist", "New Description here");
            Playlist result = PlaylistDataAccessLayer.getPlaylistById(context, 1);

            // Compare
            if (result != null && result.playlistName.equals("New Name Playlist") && result.playlistDescription.equals("New Description here")) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " Expected: New Name Playlist | New Description here";
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete

        return res;
    }

    public static String TestGetChordByName(Context context) {
        String res = "TestGetChordByName: ";
        try {
            // Create
            Chord chord = new Chord(2, "Am");

            // Insert (set Am & E is the chords of Quoc Ca)
            ChordDataAccessLayer.insertChord(context, chord);

            // Get
            Chord result = ChordDataAccessLayer.getChordByName(context, "Am");

            // Compare
            if (result != null && result.equals(chord)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " Expected: " + chord.toString();
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        ChordDataAccessLayer.removeChord(context, 2);
        return res;
    }

    public static String TestSearchSongByTitle(Context context) {
        String res = "TestGetChordByName: ";
        try {
            // Create
            Song song1 = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date());
            Song song2 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Ken m", "www.55555.com", " len nam chau vo mau giao", "chau len nam", new Date());
            Song song3 = new Song(Config.DEFAULT_SONG_ID, 6, "Muoi ", "www.6666666.com", "chau len 666666nam chau vo mau giao", "chau66666 len nam", new Date());


            // Insert
            SongDataAccessLayer.insertSong(context, song1);
            SongDataAccessLayer.insertSong(context, song2);
            SongDataAccessLayer.insertSong(context, song3);

            // Get
            List<Song> results = SongDataAccessLayer.searchSongByTitle(context, "Chau", Config.DEFAULT_SEARCH_LIMIT);

            // Compare
            if (results.size() == 2 && results.get(0).equals(song2) && results.get(1).equals(song1)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + results + " Expected: " + song2.toString() + "|" + song1.toString();
            }

            // Get 2
            List<Song> results2 = SongDataAccessLayer.searchSongByTitle(context, "m", Config.DEFAULT_SEARCH_LIMIT);

            // Compare 2
            if (results2.size() == 1 && results2.get(0).equals(song3)) {
                res += " OK";
            } else {
                res += " FAIL: results2: " + results2 + " Expected: " + song3.toString();
            }

        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);
        SongDataAccessLayer.removeSongById(context, 5);
        SongDataAccessLayer.removeSongById(context, 6);
        return res;
    }

    public static String TestGetArtistByName(Context context) {

        String res = "TestGetArtistByName: ";
        try {
            // Create
            Artist a1 = new Artist(1, "Huỳnh Quang Thảo", "Huynh Quang Thao");
            Artist a2 = new Artist(2, "Ngô Trắc Kiện", "Ngo Trac Kien");

            // Insert
            ArtistDataAcessLayer.insertArtist(context, a1);
            ArtistDataAcessLayer.insertArtist(context, a2);

            // Get
            Artist result1 = ArtistDataAcessLayer.getArtistByName(context, "huynh quang thao");
            Artist result2 = ArtistDataAcessLayer.getArtistByName(context, "ngo trac kien");

            // Compare
            if (result1 != null && result2 != null && result1.equals(a1) && result2.equals(a2)) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result1 + "|" + result2 + " Expected: " + a1 + "|" + a2;
            }
        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        return res;
    }

    public static String TestSearchSongByArtist(Context context) {
        String res = "TestSearchSongByArtist: ";
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

            Song song1 = new Song(Config.DEFAULT_SONG_ID, 4, "Chau Len bon", "www.4444444.com", "chau len bon chau vo mau giao", "chau len bon", new Date(), inputA, inputC, inputS);
            Song song2 = new Song(Config.DEFAULT_SONG_ID, 5, "Chau Len nam", "www.55555.com", "chau len nam chau vo mau giao", "chau len nam", new Date(), inputA, inputC, inputS);


            // Insert
            SongDataAccessLayer.insertSong(context, song1);
            SongDataAccessLayer.insertSong(context, song2);

            ArtistDataAcessLayer.insertListOfArtists(context, inputA);
            ArtistDataAcessLayer.insertListOfArtists(context, inputS);

            ChordDataAccessLayer.insertListOfChords(context, inputC);

            SongArtistDataAccessLayer.insertSong_Author(context, 4, 1);
            SongArtistDataAccessLayer.insertSong_Author(context, 4, 2);
            SongArtistDataAccessLayer.insertSong_Author(context, 5, 2);

            SongArtistDataAccessLayer.insertSong_Singer(context, 5, 1);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 3);
            SongArtistDataAccessLayer.insertSong_Singer(context, 4, 4);
            SongArtistDataAccessLayer.insertSong_Singer(context, 5, 4);


            SongChordDataAccessLayer.insertSong_Chord(context, 4, 1);
            SongChordDataAccessLayer.insertSong_Chord(context, 4, 2);


            // Get
            // should be song1
            List<Song> result = ArtistDataAcessLayer.searchSongByArtist(context, "dinh quang trung", Config.DEFAILT_SEARCH_ARTIST_LIMIT);

            // Compare
            if (result.size() == 1 && (result.get(0).equals(song1) || result.get(0).equals(song2))) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result.toString() + " expected: " + song1.toString();
            }

            // Get
            // should be song1 and song2
            List<Song> result2 = ArtistDataAcessLayer.searchSongByArtist(context, "huynh quang thao", Config.DEFAILT_SEARCH_ARTIST_LIMIT);

            // Compare
            if (result2.size() == 2 && ((result2.get(0).equals(song1) && result2.get(1).equals(song2)) ||
                    (result2.get(0).equals(song2) && result2.get(1).equals(song1)))) {
                res += " OK";
            } else {
                res += " FAIL: result: " + result2.toString() + " expected: " + song1.toString() + " | " + song2.toString();
            }


        } catch (Exception e) {
            res += "Exception: " + e.toString();
            e.printStackTrace();
        }

        // Delete
        SongDataAccessLayer.removeSongById(context, 4);
        SongDataAccessLayer.removeSongById(context, 5);

        ArtistDataAcessLayer.removeArtistByid(context, 1);
        ArtistDataAcessLayer.removeArtistByid(context, 2);
        ArtistDataAcessLayer.removeArtistByid(context, 3);
        ArtistDataAcessLayer.removeArtistByid(context, 4);

        ChordDataAccessLayer.removeChord(context, 1);
        ChordDataAccessLayer.removeChord(context, 2);

        SongArtistDataAccessLayer.removeSong_Author(context, 4, 1);
        SongArtistDataAccessLayer.removeSong_Author(context, 4, 2);
        SongArtistDataAccessLayer.removeSong_Author(context, 5, 2);

        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 3);
        SongArtistDataAccessLayer.removeSong_Singer(context, 4, 4);
        SongArtistDataAccessLayer.removeSong_Singer(context, 5, 4);

        SongChordDataAccessLayer.removeSong_Chord(context, 4, 1);
        SongChordDataAccessLayer.removeSong_Chord(context, 4, 2);
        return res;
    }
}
