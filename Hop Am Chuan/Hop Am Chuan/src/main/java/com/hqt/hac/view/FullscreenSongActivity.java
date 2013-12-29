package com.hqt.hac.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.widget.SlidingMenuActionBarActivity;
import com.hqt.hac.model.Song;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.ScreenUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import static com.hqt.hac.utils.LogUtils.LOGE;

public class FullscreenSongActivity extends SlidingMenuActionBarActivity {
    /**
     * My self *
     */
    private Activity that = this;

    /**
     * View for side bar
     */
    private View sidebarView;

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
    private TextView fontSizeTV;
    private SeekBar fontSizeSB;

    private Dialog dialogScroll;
    private TextView scrollTV;
    private SeekBar scrollSB;
    private Button toTopBtn;
    private CheckBox turnOnChk;

    private Dialog dialogTranspose;
    private Button transUpBtn;
    private Button transDownBtn;

    private Dialog dialogMusic;


    /**
     * Controls *
     */
    // The text view
    private TextView songContentTV;
    // Scroll view for smoothie and auto scroll function
    private ScrollView scrollView;

    // Auto scroll thread
    private AutoScrollThread scroller;

    /**
     * Lyric view mode *
     */
    private boolean singleLineMode = true;
    private int speed = Config.SONG_AUTO_SCROLL_MIN_NEV_SPEED;
    private boolean autoScroll = false;

    /** This is used to keep the screen always on **/
    protected PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // front view
        setContentView(R.layout.activity_song_fullscreen);

        // behind view
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        sidebarView = inflater.inflate(R.layout.song_right_bar, null);
        setBehindContentView(sidebarView);

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

        // Start auto scroll thread
        scroller = new AutoScrollThread();
        // scroller.start();

        // Keep the screen always on
        setScreenOn();

    }

    private void setScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setUpDialogs() {
        dialogFontSize = DialogUtils.createDialog(this, R.string.song_detail_fontsize,
                getLayoutInflater(), R.layout.dialog_songdetail_fontsize);
        fontSizeTV = (TextView) dialogFontSize.findViewById(R.id.fontSizeTV);
        fontSizeSB = (SeekBar) dialogFontSize.findViewById(R.id.fontSizeSB);

        fontSizeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                fontSizeTV.setText(String.valueOf(value + 1));
                songContentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, value + 1);
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
        scrollTV = (TextView) dialogScroll.findViewById(R.id.scrollTV);
        scrollSB = (SeekBar) dialogScroll.findViewById(R.id.scrollSB);
        toTopBtn = (Button) dialogScroll.findViewById(R.id.toTopBtn);
        turnOnChk = (CheckBox) dialogScroll.findViewById(R.id.turnOnChk);

        scrollSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean isUserTouched) {
                if (isUserTouched) {
                    speed = value + Config.SONG_AUTO_SCROLL_MIN_NEV_SPEED;
                    scrollTV.setText(String.valueOf(speed));
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
        turnOnChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                autoScroll = b;
            }
        });

        dialogTranspose = DialogUtils.createDialog(this, R.string.song_detail_transpose,
                getLayoutInflater(), R.layout.dialog_songdetail_transpose);
        transUpBtn = (Button) dialogTranspose.findViewById(R.id.btnTransUp);
        transDownBtn = (Button) dialogTranspose.findViewById(R.id.btnTransDown);


        transUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), songContentTV, 1, that);
            }
        });
        transDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), songContentTV, -1, that);
            }
        });
    }

    private void setUpControlsAndEvents() {
        // Set content controls
        songContentTV = (TextView) findViewById(R.id.songContent);

        // Scroll view
        scrollView = (ScrollView) findViewById(R.id.songContentScrollView);

        // Set sidebar controls
        Button btnFont = (Button) findViewById(R.id.btnFont);
//        Button btnFontDown = (Button) findViewById(R.id.btnFontDown);
        Button btnLineModeToggle = (Button) findViewById(R.id.btnLineModeToggle);
        Button btnScroll = (Button) findViewById(R.id.btnScroll);
        Button btnTrans = (Button) findViewById(R.id.btnTrans);
        ImageButton toTopShortcutBtn = (ImageButton) findViewById(R.id.toTopShortcutBtn);

        // Events for controls
        btnFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close sliding menu
                sidebar.showContent();
                sidebar.setEnabled(false);

                // Set value
                fontSizeTV.setText(String.valueOf(songContentTV.getTextSize()));
                fontSizeSB.setProgress((int) songContentTV.getTextSize());

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
                scrollTV.setText(String.valueOf(speed));
                scrollSB.setProgress(speed - 1);

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
                if (singleLineMode) {
                    HacUtils.setSongFormatted(getApplicationContext(), songContentTV, song.getContent(getApplicationContext()), that);
                    songContentTV.setTypeface(Typeface.DEFAULT);
                } else {
                    HacUtils.setSongFormattedTwoLines(getApplicationContext(), songContentTV, song.getContent(getApplicationContext()), that);
                    songContentTV.setTypeface(Typeface.MONOSPACE);
                }
            }
        });
        toTopShortcutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.scrollTo(0, 0);
            }
        });
    }

    private void setUpContent() {
        HacUtils.setSongFormatted(
                getApplicationContext(),
                songContentTV,
                song.getContent(getApplicationContext()),
                this);
        songContentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, Config.SONG_CONTENT_DEFAULT_FONT_SIZE);
        int paddingTop = ScreenUtils.getScreenSize(this).y / 2;
        songContentTV.setPadding(
                Config.SONG_CONTENT_DEFAULT_PADDING,
                paddingTop,
                Config.SONG_CONTENT_DEFAULT_PADDING,
                Config.SONG_CONTENT_DEFAULT_PADDING);
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGE("TRUNGDQ", "resume now!");
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
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

    private class AutoScrollThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    if (autoScroll) {
                        sleep(Config.SONG_AUTO_SCROLL_MAX_NEV_SPEED / speed * Config.SONG_AUTO_SCROLL_DEGREE);
                        scrollView.smoothScrollBy(0, (int) Math.sqrt(Math.sqrt(speed)));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
