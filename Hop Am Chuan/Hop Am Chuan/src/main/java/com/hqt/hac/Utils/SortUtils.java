package com.hqt.hac.utils;

import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dao.SongArtistDataAccessLayer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * this class using for sort again array or ArrayList with parameters
 */
public class SortUtils {

    /**
     * use this class for all types of comparators that we need
     */
    public static class ComparatorObject {
        public static Comparator<Song> compareSongByABC() {
            return new Comparator<Song>() {
                @Override
                public int compare(Song s1, Song s2) {
                    return s1.title.compareTo(s2.title);
                }
            };
        }
        public static Comparator<Song> compareSongByDate() {
            return new Comparator<Song>() {
                @Override
                public int compare(Song lhs, Song rhs) {
                    return lhs.lastView - rhs.lastView;
                }
            };
        }

        public static Comparator<Artist> compareArtistByABC() {
            return new Comparator<Artist>() {
                @Override
                public int compare(Artist lhs, Artist rhs) {
                    return lhs.artistName.compareTo(rhs.artistName);
                }
            };
        }

        public static Comparator<Artist> compareArtistByDateCreate() {
            return new Comparator<Artist>() {
                @Override
                public int compare(Artist lhs, Artist rhs) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        public static Comparator<Artist> compareArtistByNumOfSongs() {
            return new Comparator<Artist>() {
                @Override
                public int compare(Artist lhs, Artist rhs) {
                    throw new UnsupportedOperationException();
                }
            };
        }


    }
    /**
     * generic method.
     * use this method for sorting object that already implements Comparable
     */
    public static<T extends Comparable> void sort(List<T> list) {
        Collections.sort(list);
    }

    /**
     * generic method
     * use this method for sorting object with Comparator function
     */
    public static<T> void sort(List<T> list, Comparator<T> comparator) {
        Collections.sort(list, comparator);
    }

    ////////////////////////////////////////////////////////////
    //////////////////// Sort non-generic method //////////////

    public static void sortSongByABC(List<Song> list) {
        sort(list, ComparatorObject.compareSongByABC());
    }

    public static void sortSongByDate(List<Song> list) {
        sort(list, ComparatorObject.compareSongByDate());
    }
}
