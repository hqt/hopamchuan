package com.hqt.hac.view;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

import java.util.concurrent.atomic.AtomicBoolean;

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
    private TextView fontSizeTextView;
    private SeekBar fontSizeSeekBar;

    private Dialog dialogScroll;
    private TextView scrollTextView;
    private SeekBar scrollSeekBar;
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
    private TextView songContentTextView;
    // Scroll view for smoothie and auto scroll function
    private ScrollView scrollView;


    /**
     * Lyric view mode *
     */
    private boolean singleLineMode = true;
    private int speed = Config.SONG_AUTO_SCROLL_MIN_NEV_SPEED;
    private AtomicBoolean isAutoScroll = new AtomicBoolean(false);

    /** velocity speed */
    int velocitySpeed =  velocitySpeedFormula();

    ScrollHandler mHandler;

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
        toTopBtn = (Button) dialogScroll.findViewById(R.id.toTopBtn);
        turnOnChk = (CheckBox) dialogScroll.findViewById(R.id.turnOnChk);

        scrollSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean isUserTouched) {
                if (isUserTouched) {
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
                isAutoScroll.set(state);
                if (isAutoScroll.get()) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(Config.SONG_AUTO_SCROLL_MAX_NEV_SPEED / speed * Config.SONG_AUTO_SCROLL_DEGREE);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mHandler.sendMessage(mHandler.obtainMessage());
                        }
                    });
                    t.start();
                } else {

                }
            }
        });

        dialogTranspose = DialogUtils.createDialog(this, R.string.song_detail_transpose,
                getLayoutInflater(), R.layout.dialog_songdetail_transpose);
        transUpBtn = (Button) dialogTranspose.findViewById(R.id.btnTransUp);
        transDownBtn = (Button) dialogTranspose.findViewById(R.id.btnTransDown);


        transUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), songContentTextView, 1, that);
            }
        });
        transDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), songContentTextView, -1, that);
            }
        });
    }

    private void setUpControlsAndEvents() {
        // Set content controls
        songContentTextView = (TextView) findViewById(R.id.songContent);

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
                if (singleLineMode) {
                    HacUtils.setSongFormatted(getApplicationContext(), songContentTextView, song.getContent(getApplicationContext()), that);
                    songContentTextView.setTypeface(Typeface.DEFAULT);
                } else {
                    HacUtils.setSongFormattedTwoLines(getApplicationContext(), songContentTextView, song.getContent(getApplicationContext()), that);
                    songContentTextView.setTypeface(Typeface.MONOSPACE);
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
                songContentTextView,
                song.getContent(getApplicationContext()),
                this);
        songContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Config.SONG_CONTENT_DEFAULT_FONT_SIZE);
        int paddingTop = ScreenUtils.getScreenSize(this).y / 2;
        songContentTextView.setPadding(
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

    private int velocitySpeedFormula() {
        return (int)Math.sqrt(Math.sqrt(speed));
    }

    private class ScrollHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            scrollView.smoothScrollBy(0, velocitySpeed);
        }
    }
}
