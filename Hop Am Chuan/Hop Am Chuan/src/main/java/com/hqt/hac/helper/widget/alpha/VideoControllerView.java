package com.hqt.hac.helper.widget.alpha;

//*

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.hqt.hac.view.R;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * @author  Huynh Quang Thao
 * This class is copied from Google Source Code with modify to comparable with old system (APIUtils <= 10)*
 * A view containing controls for a MediaPlayer. Typically contains the
 * buttons like "Play/Pause", "Rewind", "Fast Forward" and a progress
 * slider. It takes care of synchronizing the controls with the state
 * of the MediaPlayer.
 * <p/>
 * The way to use this class is to instantiate it programatically.
 * The MediaController will create a default set of controls
 * and put them in a window floating above your application. Specifically,
 * the controls will float above the view specified with setAnchorView().
 * The window will disappear if left idle for three seconds and reappear
 * when the user touches the anchor view.
 * <p/>
 * Functions like show() and hide() have no effect when MediaController
 * is created in an xml layout.
 * <p/>
 * MediaController will hide and
 * show the buttons according to these rules:
 * <ul>
 * <li> The "previous" and "next" buttons are hidden until setupPreviousNextListeners()
 * has been called
 * <li> The "previous" and "next" buttons are visible but disabled if
 * setupPreviousNextListeners() was called with null listeners
 * <li> The "rewind" and "fastforward" buttons are shown unless requested
 * otherwise by using the MediaController(Context, boolean) constructor
 * with the boolean set to false
 * </ul>
 */
public class VideoControllerView extends FrameLayout {

    private static final String TAG = makeLogTag(VideoControllerView.class);

    //region Custom Variable Declaration
    /** interface that which class use this view must implement */
    private MediaPlayerControl player;
    /** Application Context */
    private Context context;
    private ViewGroup mAnchor;
    private View mRoot;
    private boolean mShowing;
    private boolean mDragging;
    private static final int sDefaultTimeout = 0;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mUseFastForward;
    private boolean mFromXml;
    private boolean mListenersSet;
    /** Listener : Use to handle action for those two button */
    private View.OnClickListener mNextListener, mPrevListener;
    /** Use StringBuilder And Formatter for format time of songs */
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    //endregion

    //region Media Player Component declaration
    /** Media Player Component declaration */

    /** Pause button for stopping currently song video*/
    private ImageButton mPauseButton;
    private ImageButton mFastForwardButton;
    private ImageButton mRewButton;
    /** See next song */
    private ImageButton mNextButton;
    /** See previous song */
    private ImageButton prevButton;
    /** We use custom Media Player protocol. So We can implement fullscreen here */
    private ImageButton fullscreenButton;
    /** Progress Bar to show time / long of video or music */
    private ProgressBar mProgress;
    /** TextView to show time detail */
    private TextView mEndTime, mCurrentTime;
    //endregion

    /** Use mHandler for processing on Multi thread */
    private Handler mHandler = new MessageHandler(this);


    //region VideoControllerView Constructor
    public VideoControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = null;
        this.context = context;
        mUseFastForward = true;
        mFromXml = true;

        Log.i(TAG, TAG);
    }

    public VideoControllerView(Context context, boolean useFastForward) {
        super(context);
        this.context = context;
        mUseFastForward = useFastForward;

        Log.i(TAG, TAG);
    }

    public VideoControllerView(Context context) {
        this(context, true);

        Log.i(TAG, TAG);
    }
    //endregion

    //region Public Method for Other Class CallƯ
    /////////////////////////////////////////////////////////////////
    //////////////////// PUBLIC METHOD FOR OTHER CLASS //////////////

    public void setMediaPlayer(MediaPlayerControl player) {
        this.player = player;
        updatePausePlay();
        updateFullScreen();
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * Use this method for dynamic adding view
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        /** create Layout as big as their parent give */
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        //removeAllViews();
        View v = makeControllerView();
        // addView(v, frameParams);
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after pre-define time seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show
     *                the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP
            );

            mAnchor.addView(this, tlp);
            mShowing = true;
        }
        updatePausePlay();
        updateFullScreen();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    /**
     * Update image for Play Button Icon
     * if player is playing, display stop button
     * if player is stopped, display playing button
     */
    public void updatePausePlay() {
        if (mRoot == null || mPauseButton == null || player == null) {
            return;
        }

        if (player.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_media_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_media_play);
        }
    }

    /**
     * Update Image for FullScreen Button Icon
     * If is fullscreen, display icon fullscreen shrink
     * if is not, display icon fullscreen sketch
     */
    public void updateFullScreen() {
        if (mRoot == null || fullscreenButton == null || player == null) {
            return;
        }

        if (player.isFullScreen()) {
            fullscreenButton.setImageResource(R.drawable.ic_media_fullscreen_shrink);
        } else {
            fullscreenButton.setImageResource(R.drawable.ic_media_fullscreen_stretch);
        }
    }

    /**
     * Set action for those two buttons
     * After set. this button will active.
     * By default, it will not active
     */
    public void setPreviousNextListeners(View.OnClickListener next, View.OnClickListener prev) {
        mNextListener = next;
        mPrevListener = prev;
        mListenersSet = true;

        if (mRoot != null) {
            // set action and enable for those two buttons
            installPrevNextListeners();

            // set visible for those two buttons
            // so users can see this buttons
            if (mNextButton != null && !mFromXml) {
                mNextButton.setVisibility(View.VISIBLE);
            }
            if (prevButton != null && !mFromXml) {
                prevButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //endregion

    //region OVERRIDE METHOD OF FRAME LAYOUT
    //////////////////////////////////////////////////////////
    /////////////////// OVERRIDE METHOD /////////////////////

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    /** Set Enable for Media Player Control */
    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mFastForwardButton != null) {
            mFastForwardButton.setEnabled(enabled);
        }
        if (mRewButton != null) {
            mRewButton.setEnabled(enabled);
        }
        if (mNextButton != null) {
            mNextButton.setEnabled(enabled && mNextListener != null);
        }
        if (prevButton != null) {
            prevButton.setEnabled(enabled && mPrevListener != null);
        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !player.isPlaying()) {
                player.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && player.isPlaying()) {
                player.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    ////////////////////////////////////////////////////////////
    //endregion  OF FRAME LA

    //region Private Method
    ////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHOD FOR THIS CLASS /////////////

    /**
     * Initialize Component Action
     * Add Listener for thos component
     * those listeners objects have been declare belown at Listener Scope
     */
    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        fullscreenButton = (ImageButton) v.findViewById(R.id.fullscreen);
        if (fullscreenButton != null) {
            fullscreenButton.requestFocus();
            fullscreenButton.setOnClickListener(mFullscreenListener);
        }

        mFastForwardButton = (ImageButton) v.findViewById(R.id.ffwd);
        if (mFastForwardButton != null) {
            mFastForwardButton.setOnClickListener(fastForwardListener);
            if (!mFromXml) {
                mFastForwardButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
            }
        }

        mRewButton = (ImageButton) v.findViewById(R.id.rew);
        if (mRewButton != null) {
            mRewButton.setOnClickListener(mRewListener);
            if (!mFromXml) {
                mRewButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
            }
        }

        // By default these are hidden. They will be enabled when setupPreviousNextListeners() is called
        mNextButton = (ImageButton) v.findViewById(R.id.next);
        if (mNextButton != null && !mFromXml && !mListenersSet) {
            mNextButton.setVisibility(View.GONE);
        }
        prevButton = (ImageButton) v.findViewById(R.id.prev);
        if (prevButton != null && !mFromXml && !mListenersSet) {
            prevButton.setVisibility(View.GONE);
        }

        mProgress = (ProgressBar) v.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(R.id.time);
        mCurrentTime = (TextView) v.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        installPrevNextListeners();
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     * Class implements this interface must declare explicity enable or not
     */
    private void disableUnsupportedButtons() {
        if (player == null) {
            return;
        }

        try {
            if (mPauseButton != null && !player.canPause()) {
                mPauseButton.setEnabled(false);
            }
            if (mRewButton != null && !player.canSeekBackward()) {
                mRewButton.setEnabled(false);
            }
            if (mFastForwardButton != null && !player.canSeekForward()) {
                mFastForwardButton.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }


    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * Set Progress on SeekBar
     *
     */
    private int setProgress() {
        if (player == null || mDragging) {
            return 0;
        }

        int position = player.getCurrentPosition();
        int duration = player.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = player.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    /**
     * Stop Or Pause Player. base on current state of player
     */
    private void doPauseResume() {
        if (player == null) {
            return;
        }

        if (player.isPlaying()) {
            player.pause();
        } else {
            player.start();
        }
        updatePausePlay();
    }

    /**
     * Make Current Media Player fullscreen or not.
     * by calling player object (class that implement {VideoControllerView} Interface
     */
    private void doToggleFullscreen() {
        if (player == null) {
            return;
        }

        player.toggleFullScreen();
        updateFullScreen();
    }

    /**
     * Set Listener for Previous Button and Next Button
     * public method setupPreviousNextListeners() will call this private method
     * this method will set listener object for button (already define in listener object scope)
     */
    private void installPrevNextListeners() {
        if (mNextButton != null) {
            mNextButton.setOnClickListener(mNextListener);
            mNextButton.setEnabled(mNextListener != null);
        }

        if (prevButton != null) {
            prevButton.setOnClickListener(mPrevListener);
            prevButton.setEnabled(mPrevListener != null);
        }
    }

    /////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////
    //endregion

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.media_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }

    //region LISTENER OBJECT
    /////////////////////////////////////////////////////////////////////
    ////////////// DECLARE LISTENER OBJECT FOR COMPONENT ////////////////
    ////////////////// OF MEDIA PLAYER CONTROL /////////////////////////
    /////// INCLUDE FASTFORWARD | FASTPREVIOUS | PAUSE | FULLSCREEN/////
    //////// ONSEEKBARCHANGE //////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////

    /**
     * Set Listener for FastForward Button
     */

    private View.OnClickListener fastForwardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }

            int pos = player.getCurrentPosition();
            // plus 15 seconds forward
            pos += 15000; // milliseconds
            // set SeekBar to this point
            player.seekTo(pos);
            // set progress of current video to this point
            setProgress();
            // re-drawn player
            show(sDefaultTimeout);
        }
    };


    /**
     * Set Listener for Previous Forward Button
     */
    private View.OnClickListener mRewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }

            int pos = player.getCurrentPosition();
            // decrease about 5 seconds
            pos -= 5000; // milliseconds
            // set player SeekBar to this point
            player.seekTo(pos);
            // Change music/video to this point
            setProgress();

            // redrawn
            show(sDefaultTimeout);
        }
    };

    /** Set Listener for Pause Button */
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    /** Set Listener for FullScreen Button */
    private View.OnClickListener mFullscreenListener = new View.OnClickListener() {
        public void onClick(View v) {
            doToggleFullscreen();
            show(sDefaultTimeout);
        }
    };

    /**
      * There are two scenarios that can trigger the seekbar listener to trigger:
        The first is the user using the touchpad to adjust the posititon of the
        seekbar's thumb. In this case onStartTrackingTouch is called followed by
        a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
        We're setting the field "mDragging" to true for the duration of the dragging
        session to avoid jumps in the position in case of ongoing playback.

        The second scenario involves the user operating the scroll ball, in this
        case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
        we will simply apply the updated position without suspending regular updates.
    */
     private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(0);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (player == null) {
                return;
            }

            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = player.getDuration();
            long newposition = (duration * progress) / 1000L;
            player.seekTo((int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime((int) newposition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };



    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    //endregion

    /**
     * Class Use this View must implement this interface to decide action of this Media Player
     */
    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int pos);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();

        boolean isFullScreen();

        void toggleFullScreen();
    }

    /**
     * Handle for controlling action
     * Handler will works on different thread to avoid hard work on UI Thread
     * Using Weak Reference here
     */
    private static class MessageHandler extends Handler {
        private final WeakReference<VideoControllerView> mView;

        MessageHandler(VideoControllerView view) {
            mView = new WeakReference<VideoControllerView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoControllerView view = mView.get();
            if (view == null || view.player == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.setProgress();
                    if (!view.mDragging && view.mShowing && view.player.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }
}