package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.service.Mp3PlayerService;
import com.hqt.hac.helper.widget.MusicPlayerController;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.ArtistDataAccessLayer;
import com.hqt.hac.model.dal.ChordDataAccessLayer;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.view.FullscreenSongActivity;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongDetailFragment extends Fragment implements MusicPlayerController.IMediaPlayerControl, IHacFragment {

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

    /** Related songs layout **/
    private LinearLayout sameAuthorLayout;
    private LinearLayout sameSingerLayout;
    private LinearLayout sameChordLayout;

    /** Popup window for related songs stars **/
    private PopupWindow popupWindow;

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
        // HacUtils.setSongFormatted(activity.getApplicationContext(), songContentTV, song.getContent(activity.getApplicationContext()), activity);
        songContentTextView.setText(song.getContent(activity.getApplicationContext()));
        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });
        songContentTextView.setSelected(true);

        // Fullscreen button
        ImageView fullScreenButton = (ImageView) rootView.findViewById(R.id.fullscreen);
        // Star menu button
        final ImageView starMenuButton = (ImageView) rootView.findViewById(R.id.songMenuBtn);

        if (song.isFavorite > 0) {
            starMenuButton.setImageResource(R.drawable.star_liked);
        } else {
            starMenuButton.setImageResource(R.drawable.star);
        }

        fullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullScreenSong();
            }
        });

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

        setupMediaPlayer();

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

        sameAuthorLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_same_author);
        sameSingerLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_same_singer);
        sameChordLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_same_chord);

        /** Same author **/
        addSameAuthorSongs();

        /** Same singer **/
        addSameSingerSongs();

        /** Same chords **/
        addSameChordSongs();

        // Action for buttons
        TextView sameChordBtn = (TextView) rootView.findViewById(R.id.same_chord_btn);
        TextView sameAuthorBtn = (TextView) rootView.findViewById(R.id.same_author_btn);
        TextView sameSingerBtn = (TextView) rootView.findViewById(R.id.same_singer_btn);

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
    private void addSameChordSongs() {
        List<Song> sameChord = ChordDataAccessLayer.getRandomSongsByChords(
                activity.getApplicationContext(),
                song.getChords(activity.getApplicationContext()),
                Config.DEFAULT_RELATED_SONGS_COUNT);
        addSongsToLayout(sameChord, sameChordLayout);
    }
    private void addSameSingerSongs() {
        List<Song> sameSinger = ArtistDataAccessLayer.getRandomSongsBySinger(
                activity.getApplicationContext(),
                song.getSingers(activity.getApplicationContext()).get(0).artistId,
                Config.DEFAULT_RELATED_SONGS_COUNT);
        addSongsToLayout(sameSinger, sameSingerLayout);
    }
    private void addSameAuthorSongs() {
        List<Song> sameAuthor = ArtistDataAccessLayer.getRandomSongsByAuthor(
                activity.getApplicationContext(),
                song.getAuthors(activity.getApplicationContext()).get(0).artistId,
                Config.DEFAULT_RELATED_SONGS_COUNT);
        addSongsToLayout(sameAuthor, sameAuthorLayout);
    }

    /**
     * Dynamically add songs to the layout.
     * @param songs
     * @param layout
     */
    private void addSongsToLayout(List<Song> songs, ViewGroup layout) {
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

    ////////////////////////////////////////////////////////////////////
    /////////////////// CONFIG MP3 PLAYER //////////////////////////////

    /** Controller for Media Player */
    MusicPlayerController controller;
    /** Android Built-in Media Player : reference object from service object */
    MediaPlayer player;
    /** ref to current Service */
    Mp3PlayerService mp3Service;

    /** setup start from here */
    private void setupMediaPlayer() {
        mp3Service = MainActivity.mp3Service;
        player = MainActivity.player;
        controller = new MusicPlayerController(rootView);
        controller.setMediaPlayer(SongDetailFragment.this);
    }

    @Override
    public void start() {
        LOGD(TAG, "Start Player");
        player.start();
        Bundle arguments = new Bundle();
        arguments.putParcelable("song", song);
        DialogUtils.createNotification(activity.getApplicationContext(), MainActivity.class, arguments,
                song.title, song.getAuthors(getActivity().getApplicationContext()).get(0).artistName, Mp3PlayerService.NOTIFICATION_ID);
    }

    @Override
    public void pause() {
        LOGD(TAG, "Pause Player");
        player.pause();
        DialogUtils.closeNotification(activity.getApplicationContext(), Mp3PlayerService.NOTIFICATION_ID);
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    /** Choosing Component here */

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }
}
