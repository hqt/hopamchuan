package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.model.Artist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.ArtistDataAccessLayer;
import com.hqt.hac.model.dal.ChordDataAccessLayer;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.view.BunnyApplication;
import com.hqt.hac.view.FullscreenSongActivity;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDetailFragment extends CustomFragment {

    private static String TAG = makeLogTag(PlaylistDetailFragment.class);

    /**
     * Main Activity for reference
     */
    MainActivity activity;

    View rootView;

    /** Song object for this fragment */
    public Song song;

    /** resource for title int */
    int mResTitle = R.string.title_activity_song_list_fragment;

    // Stuff for popup menu
    private PopupWindow mPopupMenu;
    private View mMenuLayout;
    private boolean isPopupOpened = false;

    /** data structure to contains all ids. prevent random return duplicate */
    Set<Integer> sameChordSongs = new HashSet<Integer>();
    Set<Integer> sameSingerSongs = new HashSet<Integer>();
    Set<Integer> sameAuthorSongs = new HashSet<Integer>();

    /** Related songs layout **/
    private LinearLayout sameAuthorLayout;
    private LinearLayout sameSingerLayout;
    private LinearLayout sameChordLayout;
    private int currentSameChordSongsCount = 0;

    /** Button to load more songs */
    TextView sameChordBtn;
    TextView sameAuthorBtn;
    TextView sameSingerBtn;

    /** Popup window for related songs stars **/
    private PopupWindow popupWindow;


    private Thread relatedSongLoad;
    private RelatedSongHandler mHandler;

    /**
     * empty constructor
     * must have for fragment
     */
    public SongDetailFragment() {

    }

    @Override
    public int getTitle() {
        return mResTitle;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;

        // get arguments from main activity
        Bundle arguments = getArguments();
        if (arguments.get("song") != null) {
            this.song = (Song) arguments.get("song");
            // set up data structure here. to prevent duplicate current song to suggestion
            sameAuthorSongs.add(song.songId);
            sameSingerSongs.add(song.songId);
            sameChordSongs.add(song.songId);
        } else {
            LOGE(TAG, "no suitable arguments to continues");
            return;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_song_detail, container, false);

        // Set song info
        TextView songTitleTextView = (TextView) rootView.findViewById(R.id.songTitle);
        TextView songAuthorsTextView = (TextView) rootView.findViewById(R.id.songAuthorsTV);
        TextView songSingersTextView = (TextView) rootView.findViewById(R.id.songSingersTV);
        TextView songContentTextView = (TextView) rootView.findViewById(R.id.songContent);
        TextView btnFullScreen = (TextView) rootView.findViewById(R.id.btnFullScreen);

        songTitleTextView.setText(song.title);
        songAuthorsTextView.setText(song.getAuthorsString(activity.getApplicationContext()));
        songSingersTextView.setText(song.getSingersString(activity.getApplicationContext()));

        // Set song content
        songContentTextView.setText(song.getContent(activity.getApplicationContext()));
        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });
        songContentTextView.setSelected(true);

        // Star menu button
        final ImageView starMenuButton = (ImageView) rootView.findViewById(R.id.songMenuBtn);
        if (song.isFavorite > 0) {
            starMenuButton.setImageResource(R.drawable.star_liked);
        } else {
            starMenuButton.setImageResource(R.drawable.star);
        }

        // Event for star menu click
        final PopupWindow popupWindows = DialogUtils.createPopup(inflater, R.layout.popup_songlist_menu);
        SongListRightMenuHandler.setRightMenuEvents(activity, popupWindows);
        starMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the popup menu and set selectedSong, theStar
                SongListRightMenuHandler.openPopupMenu(view, song, starMenuButton);
            }
        });

        // View author, singer songs
        LinearLayout songSingers = (LinearLayout) rootView.findViewById(R.id.songSingers);
        LinearLayout songAuthors = (LinearLayout) rootView.findViewById(R.id.songAuthors);

        songAuthors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Artist artist = song.getAuthors(activity.getApplicationContext()).get(0);
                ArtistViewFragment fragment = new ArtistViewFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("artist", artist);
                fragment.setArguments(arguments);
                activity.switchFragmentNormal(fragment);
                activity.changeTitleBar(artist.artistName);
            }
        });
        songSingers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Artist artist = song.getSingers(activity.getApplicationContext()).get(0);
                ArtistViewFragment fragment = new ArtistViewFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelable("artist", artist);
                fragment.setArguments(arguments);
                activity.switchFragmentNormal(fragment);
                activity.changeTitleBar(artist.artistName);
            }
        });

        // Load related songs, asynchronously.
        setUpRelatedSongs();

        return rootView;
    }

    private void openFullScreenSong() {
        Intent intent = new Intent(activity, FullscreenSongActivity.class);
        intent.putExtra("song", song);
        activity.startActivity(intent);
    }

    /**
     * TODO: do this in another thread to maintain performance
     */
    private void setUpRelatedSongs() {

        mHandler = new RelatedSongHandler();
        relatedSongLoad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Config.LOADING_SMOOTHING_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });
        relatedSongLoad.start();

    }

    private void addSameChordSongs() {
        try {
            List<Song> sameChord = ChordDataAccessLayer.getRandomSongsByChords(
                    activity.getApplicationContext(),
                    song.getChords(activity.getApplicationContext()),
                    currentSameChordSongsCount,
                    Config.DEFAULT_RELATED_SONGS_COUNT);
            // avoid duplicate
            sameChord = removeDuplicateSongs(sameChordSongs, sameChord);
            if (sameChord.size() == 0) {
                if (sameChordSongs.size() > 1)
                    Toast.makeText(
                            BunnyApplication.getAppContext(),
                            R.string.no_more_song,
                            Toast.LENGTH_LONG).show();
                // LOGE("TRUNGDQ", "sameChordSongs 2: " + sameChordSongs.size());
                if (sameChordSongs.size() == 1) {
                    (rootView.findViewById(R.id.chords_layout)).setVisibility(View.GONE);
                } else {
                    sameChordBtn.setVisibility(View.GONE);
                }
            } else {
                currentSameChordSongsCount += sameChord.size();
                addSongsToLayout(sameChord, sameChordLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addSameSingerSongs() {
        try {
            List<Song> sameSinger = ArtistDataAccessLayer.getRandomSongsBySinger(
                    activity.getApplicationContext(),
                    song.getSingers(activity.getApplicationContext()).get(0).artistId,
                    Config.DEFAULT_RELATED_SONGS_COUNT);
            // avoid duplicate
            sameSinger = removeDuplicateSongs(sameSingerSongs, sameSinger);
            if (sameSinger.size() == 0) {
                if (sameSingerSongs.size() > 1)
                    Toast.makeText(
                            BunnyApplication.getAppContext(),
                            R.string.no_more_song,
                            Toast.LENGTH_LONG).show();
                LOGE("TRUNGDQ", "sameSingerSongs: " + sameSingerSongs.size());
                if (sameSingerSongs.size() == 1) {
                    (rootView.findViewById(R.id.singers_layout)).setVisibility(View.GONE);
                } else {
                    sameSingerBtn.setVisibility(View.GONE);
                }
            } else {
                addSongsToLayout(sameSinger, sameSingerLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addSameAuthorSongs() {
        try {
            List<Song> sameAuthor = ArtistDataAccessLayer.getRandomSongsByAuthor(
                    activity.getApplicationContext(),
                    song.getAuthors(activity.getApplicationContext()).get(0).artistId,
                    Config.DEFAULT_RELATED_SONGS_COUNT);
            // avoid duplicate
            sameAuthor = removeDuplicateSongs(sameAuthorSongs, sameAuthor);
            if (sameAuthor.size()== 0) {
                if (sameAuthorSongs.size() > 1)
                    Toast.makeText(
                            BunnyApplication.getAppContext(),
                            R.string.no_more_song,
                            Toast.LENGTH_LONG).show();
                // LOGE("TRUNGDQ", "sameAuthorSongs: " + sameAuthorSongs.size());
                if (sameAuthorSongs.size() == 1) {
                    (rootView.findViewById(R.id.authors_layout)).setVisibility(View.GONE);
                } else {
                    sameAuthorBtn.setVisibility(View.GONE);
                }
            } else {
                addSongsToLayout(sameAuthor, sameAuthorLayout);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Song> removeDuplicateSongs(Set<Integer> currentSongIds, List<Song> newSongs) {
        List<Song> res = new ArrayList<Song>();
        for (Song song : newSongs) {
            if (!currentSongIds.contains(song.songId)) {
                res.add(song);
                // update data
                currentSongIds.add(song.songId);
            }
        }
        return res;
    }

    /**
     * Dynamically add songs to the layout.
     * @param songs
     * @param layout
     */
    private void addSongsToLayout(List<Song> songs, ViewGroup layout) {
        if (songs == null) return;
        for (final Song song : songs) {

            View songView = activity.getLayoutInflater().inflate(R.layout.song_detail_fragment_related_song_item, null);

            RelativeLayout songItemHolder = (RelativeLayout) songView.findViewById(R.id.relativelayout);
            LinearLayout songLinearLayout = (LinearLayout) songView.findViewById(R.id.linearLayout);

            TextView songTitle = (TextView) songItemHolder.findViewById(R.id.txtSongName);
            TextView songLyric = (TextView) songItemHolder.findViewById(R.id.txtLyrics);
            TextView songChords = (TextView) songItemHolder.findViewById(R.id.txtChord);
            final ImageView songStar = (ImageView) songItemHolder.findViewById(R.id.imageFavorite);


            songTitle.setText(song.title);
            songLyric.setText(song.firstLyric);
            songChords.setText(song.getChordString(activity.getApplicationContext()));

            if (song.isFavorite > 0) {
                songStar.setImageResource(R.drawable.star_liked);
            } else {
                songStar.setImageResource(R.drawable.star);
            }

            // Bind event to the star
            popupWindow = DialogUtils.createPopup(activity.getLayoutInflater(), R.layout.popup_songlist_menu);
            SongListRightMenuHandler.setRightMenuEvents(activity, popupWindow);

            songStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SongListRightMenuHandler.openPopupMenu(view, song, songStar);
                }
            });

            // Bind event to the song
            songLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SongDetailFragment fragment = new SongDetailFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("song", song);
                    fragment.setArguments(arguments);
                    activity.switchFragmentNormal(fragment);
                    activity.changeTitleBar(song.title);
                }
            });

            layout.addView(songItemHolder);
        }
    }

    private class RelatedSongHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            sameAuthorLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_same_author);
            sameSingerLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_same_singer);
            sameChordLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_same_chord);

            /** Same chords **/
            addSameChordSongs();

            /** Same singer **/
            addSameSingerSongs();

            /** Same author **/
            addSameAuthorSongs();

            // Action for buttons
            sameChordBtn = (TextView) rootView.findViewById(R.id.same_chord_btn);
            sameAuthorBtn = (TextView) rootView.findViewById(R.id.same_author_btn);
            sameSingerBtn = (TextView) rootView.findViewById(R.id.same_singer_btn);

            sameChordBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addSameChordSongs();
                }
            });
            sameSingerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addSameSingerSongs();
                }
            });
            sameAuthorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addSameAuthorSongs();
                }
            });
        }
    }
}
