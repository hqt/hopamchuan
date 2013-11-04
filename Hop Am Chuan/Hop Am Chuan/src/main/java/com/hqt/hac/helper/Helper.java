package com.hqt.hac.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.ArtistDataAcessLayer;
import com.hqt.hac.model.dao.ChordDataAccessLayer;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.model.dao.SongArtistDataAccessLayer;
import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.provider.HopAmChuanDBContract;
import com.hqt.hac.utils.ParserUtils;

import java.util.Date;
import java.util.List;

public class Helper {


    public static Drawable getDrawableFromResId(Context context, int id) {
        return context.getResources().getDrawable(id);
    }

    public static String arrayToString(List list) {
        String res = "";
        for (Object o : list) {
            res += o.toString() + "\n";
        }
        return res;
    }

}
