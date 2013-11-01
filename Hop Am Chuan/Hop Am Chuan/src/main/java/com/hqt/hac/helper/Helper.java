package com.hqt.hac.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.hqt.hac.Utils.ParserUtils;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.ArtistDataAcessLayer;
import com.hqt.hac.model.dao.ChordDataAccessLayer;
import com.hqt.hac.model.dao.SongDataAccessLayer;

import java.util.List;

public class Helper {

    /**
     * Use this method for easier development phrase
     */
    public static void prepareLocalDatabase(Context context) {
        // create song database
        List<Song> songs = ParserUtils.getAllSongsFromResource(context);
        SongDataAccessLayer.insertListOfSongs(context, songs);

        // create artist database
        List<Artist> artists = ParserUtils.getAllArtistsFromFRescource(context);
        ArtistDataAcessLayer.insertListOfArtists(context, artists);

        // create chord database
        List<Chord> chords = ParserUtils.getAllChordsFromResource(context);
        ChordDataAccessLayer.insertListOfChords(context, chords);
    }

    public static Drawable getDrawableFromResId(Context context, int id) {
        return context.getResources().getDrawable(id);
    }

}
