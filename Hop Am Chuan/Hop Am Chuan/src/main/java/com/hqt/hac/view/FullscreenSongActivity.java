package com.hqt.hac.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hqt.hac.config.Config;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.helper.service.Mp3PlayerService;
import com.hqt.hac.helper.widget.MusicPlayerController;
import com.hqt.hac.helper.widget.SlidingMenuActionBarActivity;
import com.hqt.hac.model.Song;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.ScreenUtils;
import com.hqt.hac.utils.UIUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGD;
import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class FullscreenSongActivity extends SlidingMenuActionBarActivity
        implements MusicPlayerController.IMediaPlayerControl {

    private static String TAG = makeLogTag(FullscreenSongActivity.class);

    /**
     * My self *
     */
    private Activity that = this;

    /**
     * Sliding Menu for Right View
     */
    private SlidingMenu sidebar;

    /**
     * The song to display *
     */
    private Song song;

    /**
     * Dialogs *
     */
    private Dialog dialogFontSize;
    private float fontSizeValue;

    private TextView fontSizeTextView;
    private SeekBar fontSizeSeekBar;

    private Dialog dialogScroll;
    private CheckBox turnOnChk;
    private int scrollSeekBarValue;

    private TextView scrollTextView;
    private SeekBar scrollSeekBar;
    private boolean firstScroll = true;

    private Dialog dialogTranspose;

    private Dialog dialogMusic;
    private View dialogMusicLayout;

    private Button btnLineModeToggle;

    /**
     *
     */
    private Thread scrollThread;
    public static int scrollX = 0;
    public static int scrollY = -1;

    /**
     * Controls *
     */
    // The text view
    private TextView songContentTextView;
    // Scroll view for smoothie and auto scroll function
    private ScrollView scrollView;


    /**
     * Lyric view mode *
     */
    private boolean singleLineMode = true;
    private int speed = Config.SONG_AUTO_SCROLL_MIN_NEV_SPEED;
    private AtomicBoolean isAutoScroll = new AtomicBoolean(false);

    /**
     * velocity speed
     */
    int velocitySpeed = velocitySpeedFormula();
    private int transposePosition = 0;

    ScrollHandler mHandler;

    /** Handler & Thread for play music using network **/
    private PlayMusicHandler playMusicHandler;
    private Thread playMusicLoad;

    /**
     *  This override method is to prevent NullPointerException in this activity
     *  see http://stackoverflow.com/questions/19275447/oncreateoptionsmenu-causing-error-in-an-activity-with-no-actionbar
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // Open right menu
            getSlidingMenu().toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Language setting
        UIUtils.setLanguage(getBaseContext());

        // front view
        setContentView(R.layout.activity_song_fullscreen);

        // behind view
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        /* View for side bar */
        View sidebarView = inflater.inflate(R.layout.song_right_bar, null);
        setBehindContentView(sidebarView);

        mHandler = new ScrollHandler();

        // Set up Sidebar
        setUpSideBar();

        // Get the song from Intent
        song = getIntent().getParcelableExtra("song");

        // Set up dialogs
        setUpDialogs();

        // Set up controls and events
        setUpControlsAndEvents();

        // Set up content
        setUpContent();

        // Keep the screen always on
        setScreenOn();

        // Media player service
        setUpMediaPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scrollX = scrollView.getScrollX();
        scrollY = scrollView.getScrollY();
    }

    private void setScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setUpDialogs() {
        dialogFontSize = DialogUtils.createDialog(this, R.string.song_detail_fontsize,
                getLayoutInflater(), R.layout.dialog_songdetail_fontsize);
        fontSizeTextView = (TextView) dialogFontSize.findViewById(R.id.fontSizeTV);
        fontSizeSeekBar = (SeekBar) dialogFontSize.findViewById(R.id.fontSizeSB);

        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                fontSizeValue = value;
                fontSizeTextView.setText(String.valueOf(value + 1));
                songContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        dialogScroll = DialogUtils.createDialog(this, R.string.song_detail_scroll,
                getLayoutInflater(), R.layout.dialog_songdetail_autoscroll);
        scrollTextView = (TextView) dialogScroll.findViewById(R.id.scrollTV);
        scrollSeekBar = (SeekBar) dialogScroll.findViewById(R.id.scrollSB);
        Button toTopBtn = (Button) dialogScroll.findViewById(R.id.toTopBtn);
        turnOnChk = (CheckBox) dialogScroll.findViewById(R.id.turnOnChk);

        scrollSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean isUserTouched) {
                if (isUserTouched) {
                    scrollSeekBarValue = value;
                    speed = value + Config.SONG_AUTO_SCROLL_MIN_NEV_SPEED;
                    velocitySpeed = velocitySpeedFormula();
                    scrollTextView.setText(String.valueOf(speed));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        toTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.scrollTo(0, 0);
            }
        });


        /** add Event for check box of Turn On */
        turnOnChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                activeScrollControl(state);
            }
        });

        dialogTranspose = DialogUtils.createDialog(this, R.string.song_detail_transpose,
                getLayoutInflater(), R.layout.dialog_songdetail_transpose);
        Button transUpBtn = (Button) dialogTranspose.findViewById(R.id.btnTransUp);
        Button transDownBtn = (Button) dialogTranspose.findViewById(R.id.btnTransDown);


        transUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transposePosition++;
                HacUtils.transposeTextView(getApplicationContext(), songContentTextView, 1, that);
            }
        });
        transDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transposePosition--;
                HacUtils.transposeTextView(getApplicationContext(), songContentTextView, -1, that);
            }
        });


        // Music dialog
        dialogMusicLayout = getLayoutInflater().inflate(R.layout.dialog_songdetail_music, null);
        dialogMusic = DialogUtils.createDialog(this, R.string.play_music, dialogMusicLayout);
        setMediaPlayerState(false, "");
    }

    private void activeScrollControl(Boolean state) {
        isAutoScroll.set(state);
        if (!isAutoScroll.get()) return;
        if (scrollThread != null) return;
        scrollThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isAutoScroll.get()) {
                        try {
                            Thread.sleep(Config.SONG_AUTO_SCROLL_MAX_NEV_SPEED / speed * Config.SONG_AUTO_SCROLL_DEGREE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                }
            }
        });
        scrollThread.start();
    }

    private void setMediaPlayerState(boolean isLoading, String statusMessage) {
        LinearLayout playerLayout = (LinearLayout) dialogMusic.findViewById(R.id.media_player_control);
        LinearLayout loadingLayout = (LinearLayout) dialogMusic.findViewById(R.id.loadingLinearLayout);
        TextView statusText = (TextView) dialogMusic.findViewById(R.id.playerStatus);
        statusText.setText(statusMessage);
        if (isLoading) {
            playerLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        } else {
            playerLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        }

    }
    private void setUpControlsAndEvents() {
        // Set content controls
        songContentTextView = (TextView) findViewById(R.id.songContent);

        // Scroll view
        scrollView = (ScrollView) findViewById(R.id.songContentScrollView);

        // Set sidebar controls
        Button btnFont = (Button) findViewById(R.id.btnFont);
        btnLineModeToggle = (Button) findViewById(R.id.btnLineModeToggle);
        Button btnScroll = (Button) findViewById(R.id.btnScroll);
        Button btnTrans = (Button) findViewById(R.id.btnTrans);
        Button btnPlayMusic = (Button) findViewById(R.id.btnPlayMusic);
        ImageButton toTopShortcutBtn = (ImageButton) findViewById(R.id.toTopShortcutBtn);

        // Events for controls
        btnFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close sliding menu
                sidebar.showContent();
                sidebar.setEnabled(false);

                // Set value
                fontSizeTextView.setText(String.valueOf(songContentTextView.getTextSize()));
                fontSizeSeekBar.setProgress((int) songContentTextView.getTextSize());

                dialogFontSize.show();
            }
        });

        btnScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close sliding menu
                sidebar.showContent();
                sidebar.setEnabled(false);

                // Set value
                scrollTextView.setText(String.valueOf(speed));
                scrollSeekBar.setProgress(speed - 1);

                // Show dialog
                dialogScroll.show();
            }
        });

        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close sliding menu
                sidebar.showContent();
                sidebar.setEnabled(false);

                // Show dialog
                dialogTranspose.show();
            }
        });

        btnLineModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleLineMode = !singleLineMode;
                activeLineModeControl();
            }
        });
        toTopShortcutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.scrollTo(0, 0);
            }
        });

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close sliding menu
                sidebar.showContent();
                sidebar.setEnabled(false);

                // Only get link if this is a new song
                if (MainActivity.mp3Service.currentSong == null
                        || MainActivity.mp3Service.currentSong.songId != song.songId) {

                    setMediaPlayerState(true, getString(R.string.media_getting_url));

                    // Setup media player, in another thread
                    playMusicHandler = new PlayMusicHandler();
                    playMusicLoad = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            // Get url from network
                            String streamUrl = APIUtils.getMp3Link(song.link);

                            // Put to message
                            Bundle bundle = new Bundle();
                            bundle.putString(Config.BUNDLE_STREAM_LINK_NAME, streamUrl);
                            Message message = new Message();
                            message.setData(bundle);

                            // Send message
                            playMusicHandler.sendMessage(message);
                        }
                    });
                    playMusicLoad.start();
                } else {
                    // If this is the openning song, then just need to update controls
                    setUpMediaControls();
                }
                // Show dialog
                dialogMusic.show();
            }
        });
    }

    private void activeLineModeControl() {
        if (singleLineMode) {
            HacUtils.setSongFormatted(getApplicationContext(), songContentTextView, song.getContent(getApplicationContext()), that);
            songContentTextView.setTypeface(Typeface.DEFAULT);
            btnLineModeToggle.setText(R.string.split_line);
        } else {
            HacUtils.setSongFormattedTwoLines(getApplicationContext(), songContentTextView, song.getContent(getApplicationContext()), that);
            songContentTextView.setTypeface(Typeface.MONOSPACE);
            btnLineModeToggle.setText(R.string.join_line);
        }
        HacUtils.transposeTextView(getApplicationContext(), songContentTextView, transposePosition, that);
    }

    private void setUpMediaControls() {
        controller = new MusicPlayerController(dialogMusicLayout);
        controller.setMediaPlayer(FullscreenSongActivity.this);
    }

    private void setUpContent() {
        HacUtils.setSongFormatted(
                getApplicationContext(),
                songContentTextView,
                song.getContent(getApplicationContext()),
                this);
        songContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Config.SONG_CONTENT_DEFAULT_FONT_SIZE);
        int paddingTop = ScreenUtils.getScreenSize(this).y / 2;
        songContentTextView.setPadding(
                Config.SONG_CONTENT_DEFAULT_PADDING,
                paddingTop,
                Config.SONG_CONTENT_DEFAULT_PADDING,
                paddingTop);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.scrollTo(scrollX, scrollY);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * NOTE* this sidebar lays from right
     */
    private void setUpSideBar() {
        sidebar = getSlidingMenu();

        // customize look for SlidingMenu
        sidebar.setMode(SlidingMenu.RIGHT);
        sidebar.setShadowWidthRes(R.dimen.shadow_width);
        sidebar.setShadowDrawable(R.drawable.shadowright);
        // recommend width for navigation drawer. use same for SlidingViewer
        sidebar.setBehindWidthRes(R.dimen.sidebar_width);
        sidebar.setFadeDegree(0.35f);

        // set custom action for SlidingMenu
        sidebar.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

    }

    private int velocitySpeedFormula() {
        return (int) Math.sqrt(Math.sqrt(speed));
    }

    private class ScrollHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            scrollView.smoothScrollBy(0, velocitySpeed);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Font size
        // Scroll position
        // Auto Scroll value
        // Auto Scroll on/off state
        // Transpose position
        // Single line mode


        outState.putFloat("fontSize", songContentTextView.getTextSize());
        outState.putInt("scrollPositionY", scrollView.getScrollY());
        outState.putBoolean("isAutoScroll", turnOnChk.isChecked());
        outState.putBoolean("firstScroll", firstScroll);
        outState.putInt("autoScrollValue", scrollSeekBarValue);
        outState.putInt("transposeValue", transposePosition);
        outState.putBoolean("singleLineMode", singleLineMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        try {
            // 1. Set content
            singleLineMode = savedInstanceState.getBoolean("singleLineMode");
            activeLineModeControl();

            // 2. Set transpose control
            transposePosition = savedInstanceState.getInt("transposeValue");
            HacUtils.transposeTextView(getApplicationContext(), songContentTextView, transposePosition, that);

            // 3. Set font size
            fontSizeValue = savedInstanceState.getFloat("fontSize");
            fontSizeTextView.setText(String.valueOf(fontSizeValue + 1));
            songContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSizeValue + 1);

            // 4. Set scroll position
            scrollView.scrollTo(0, savedInstanceState.getInt("scrollPositionY"));

            // 5. Set auto scroll control
            firstScroll = savedInstanceState.getBoolean("firstScroll");
            turnOnChk.setChecked(savedInstanceState.getBoolean("isAutoScroll"));
            scrollSeekBarValue = savedInstanceState.getInt("autoScrollValue");
            scrollSeekBar.setProgress(scrollSeekBarValue);
            speed = scrollSeekBarValue + Config.SONG_AUTO_SCROLL_MIN_NEV_SPEED;
            velocitySpeed = velocitySpeedFormula();
            scrollTextView.setText(String.valueOf(speed));
            activeScrollControl(turnOnChk.isChecked());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //region Mp3 Player Control Configuration
    ////////////////////////////////////////////////////////////////////
    /////////////////// CONFIG MP3 PLAYER //////////////////////////////

    /** Controller for Media Player */
    MusicPlayerController controller;
    /** Android Built-in Media Player : reference object from service object */
    // MediaPlayer player;
    /** ref to current Service */
    // Mp3PlayerService mp3Service;

    private void setUpMediaPlayer() {
        // get reference from Activity
        MainActivity.player.setLooping(true);

        // On buffered
        MainActivity.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // LOGE("TRUNGDQ", "current song prepared!");
                setMediaPlayerState(false, "");
                start();
                // set controller for Android Built-in Media Player
                setUpMediaControls();
            }
        });

    }

    /** setup start from here */
    private void startMediaPlayer() {
        // Only get link if this is a new song
        if (MainActivity.mp3Service.currentSong == null || MainActivity.mp3Service.currentSong.songId != song.songId) {
            // Set status
            setMediaPlayerState(true, getString(R.string.media_buffering_file));

            if (song == null || song.link == null || song.link.isEmpty()) {
                setMediaPlayerState(true, getString(R.string.media_url_fail));
            } else {
                // start currently song (not new song)
                Intent mp3ServiceIntent = new Intent(this, Mp3PlayerService.class);
                mp3ServiceIntent.putExtra("song", song);
                // LOGE(TAG, "Start music Dialog");
                startService(mp3ServiceIntent);
            }
        }
    }

    @Override
    public void start() {
        LOGD(TAG, "Start Player");
        MainActivity.player.start();
        Bundle arguments = new Bundle();
        arguments.putParcelable("song", song);
        DialogUtils.createNotification(getApplicationContext(), MainActivity.class, arguments,
                song.title, song.getAuthors(getApplicationContext()).get(0).artistName, Mp3PlayerService.NOTIFICATION_ID);
    }

    @Override
    public void pause() {
        LOGD(TAG, "Pause Player");
        MainActivity.player.pause();
        DialogUtils.closeNotification(getApplicationContext(), Mp3PlayerService.NOTIFICATION_ID);
    }

    @Override
    public int getDuration() {
        return MainActivity.player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return MainActivity.player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        MainActivity.player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return MainActivity.player.isPlaying();
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
    //endregion

    /** Handler for media player to prevent UI freezing when get mp3 data from network. **/
    private class PlayMusicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null && msg.getData() != null) {
                song.link = msg.getData().getString(Config.BUNDLE_STREAM_LINK_NAME);
            }
            startMediaPlayer();
        }
    }
}
