package com.hqt.hac.model;

import android.content.Context;

import com.hqt.hac.model.dao.SongDataAccessLayer;
import com.hqt.hac.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.hqt.hac.model.dao.SongDataAccessLayer.getAuthorsBySongId;
import static com.hqt.hac.model.dao.SongDataAccessLayer.getChordsBySongId;
import static com.hqt.hac.model.dao.SongDataAccessLayer.getSingersBySongId;
import static com.hqt.hac.model.dao.SongDataAccessLayer.getSongContent;

public class Song implements Serializable {

    public int id = 0;
    public int songId;
    public String title;
    public String link;
    public String firstLyric;
    public Date date;
    public String titleAscii;
    public int lastView;
    public int isFavorite;
    public String rhythm;

    // private modifier for lazy loading
    private String content;
    private List<Artist> authors;
    private List<Chord> chords;
    private List<Artist> singers;

    /**
     * Constructor with full information.
     * info - array - manual
     */
    public Song(int id, int songId, String title, String link, String content, String firstLyric, Date date,
                List<Artist> authors, List<Chord> chords, List<Artist> singers,
                String titleAscii, int lastView, int isFavorite, String rhythm) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
        this.authors = authors;
        this.chords = chords;
        this.singers = singers;
        this.titleAscii = titleAscii;
        this.lastView = lastView;
        this.isFavorite = isFavorite;
        this.rhythm = rhythm;
    }

    /**
     * Constructor with arrays and additional fields (default lastView, not in favorite, auto-generate titleAscii, null rhythm)
     * info - array - auto
     */
    public Song(int id, int songId, String title, String link, String content, String firstLyric, Date date,
                List<Artist> authors, List<Chord> chords, List<Artist> singers) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
        this.authors = authors;
        this.chords = chords;
        this.singers = singers;
        this.titleAscii = StringUtils.removeAcients(title);
        this.lastView = 0;
        this.isFavorite = 0;
    }

    /**
     * Constructor with no arrays and manual-set additional fields.
     * info - no - manual
     */
    public Song(int id, int songId, String title, String link, String content, String firstLyric, Date date, String titleAscii, int lastView, int isFavorite, String rhythm) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
        this.titleAscii = titleAscii;
        this.lastView = lastView;
        this.isFavorite = isFavorite;
        this.rhythm = rhythm;
    }

    /**
     * Constructor with no arrays and default additional fields
     * info - no - auto
     */
    public Song(int id, int songId, String title, String link, String content, String firstLyric, Date date) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.content = content;
        this.firstLyric = firstLyric;
        this.date = date;
        this.titleAscii = StringUtils.removeAcients(title);
        this.lastView = 0;
        this.isFavorite = 0;
    }

    /**
     * Constructor with no private fields, manual set additional fields
     */
    public Song(int id, int songId, String title, String link, String firstLyric, Date date,
                String titleAscii, int lastView, int isFavorite, String rhythm) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.firstLyric = firstLyric;
        this.date = date;
        this.titleAscii = titleAscii;
        this.lastView = lastView;
        this.isFavorite = isFavorite;
        this.rhythm = rhythm;
    }

    /**
     * Constructor with no private fields, auto-generate additional fields
     */
    public Song(int id, int songId, String title, String link, String firstLyric, Date date) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.link = link;
        this.firstLyric = firstLyric;
        this.date = date;
        this.titleAscii = StringUtils.removeAcients(title);
        this.lastView = 0;
        this.isFavorite = 0;
    }

    /**
     * Lazy loading getters
     */
    public String getContent(Context context) {
        if (this.content == null || this.content.isEmpty()) {
            this.content = getSongContent(context, songId);
            // Re-assign the last view.
            SongDataAccessLayer.setLastestView(context, songId);
        }
        return content;
    }

    public List<Artist> getAuthors(Context context) {
        if (this.authors == null) {
            this.authors = getAuthorsBySongId(context, songId);
        }
        return authors;
    }

    public List<Chord> getChords(Context context) {
        if (this.chords == null) {
            this.chords = getChordsBySongId(context, songId);
        }
        return chords;
    }

    public List<Artist> getSingers(Context context) {
        if (this.singers == null) {
            this.singers = getSingersBySongId(context, songId);
        }
        return singers;
    }

    public String getChordString(Context context) {
        List<Chord> _chords = getChords(context);
        StringBuilder result = new StringBuilder();
        for (Chord _chord : _chords) {
            result.append(_chord.name + ", ");
        }
        if (result.toString().length() > 2) {
            // Delete the last two character: ", "
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }
    public String getAuthorsString(Context context) {
        List<Artist> _authors = getAuthors(context);
        StringBuilder result = new StringBuilder();
        for (Artist _author : _authors) {
            result.append(_author.artistName + ", ");
        }
        if (result.toString().length() > 2) {
            // Delete the last two character: ", "
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }
    public String getSingersString(Context context) {
        List<Artist> _singers = getSingers(context);
        StringBuilder result = new StringBuilder();
        for (Artist _singer : _singers) {
            result.append(_singer.artistName + ", ");
        }
        if (result.toString().length() > 2) {
            // Delete the last two character: ", "
            result.deleteCharAt(result.length() - 1);
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Setters
     */
    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthors(List<Artist> authors) {
        this.authors = authors;
    }

    public void setChords(List<Chord> chords) {
        this.chords = chords;
    }

    public void setSingers(List<Artist> singers) {
        this.singers = singers;
    }


    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", songId=" + songId +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", firstLyric='" + firstLyric + '\'' +
                ", date=" + date +
                ", titleAscii='" + titleAscii + '\'' +
                ", lastView=" + lastView +
                ", isFavorite=" + isFavorite +
                ", rhythm='" + rhythm + '\'' +
                ", content='" + content + '\'' +
                ", authors=" + authors +
                ", chords=" + chords +
                ", singers=" + singers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (songId != song.songId) return false;
        if (!firstLyric.equals(song.firstLyric)) return false;
        if (!link.equals(song.link)) return false;
        if (!title.equals(song.title)) return false;
        if (!titleAscii.equals(song.titleAscii)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + link.hashCode();
        result = 31 * result + firstLyric.hashCode();
        result = 31 * result + titleAscii.hashCode();
        return result;
    }
}