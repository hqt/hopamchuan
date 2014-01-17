package com.hac.android.helper.widget;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hac.android.utils.LogUtils;
import com.hac.android.guitarchord.R;

import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

/**
 * Controller for Media Player. include play/pause/ffast/fwind/ ... function
 * and control time seeker, event when click each button etc ...
 *
 * Usage:
 *      //TODO add usage here
 *
 * Created by ThaoHQSE60963 on 12/29/13.
 */
public class MusicPlayerController {

    private static String TAG = LogUtils.makeLogTag(MusicPlayerController.class);

    /** interface that which class use this view must implement */
    private IMediaPlayerControl player;
    /** Root View that contains control of Media Player */
    private View mRootView;
    /** variable to control should show currently Media Player or not */
    private boolean mShowing = true;
    private boolean mDragging;
    /** Time out to hide Media Player. 0 means inf */
    private static final int sDefaultTimeout = 0;
    /** Time to fade out Media Player */
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mUseFastForward;
    /** variable to control should use Next/Previous song or not. Default is false : not use this function */
    private boolean isNextSongFunction = true;
    /** Listener : Use to handle action for next and previous song buttons */
    private View.OnClickListener mNextListener, mPrevListener;
    /** Use StringBuilder And Formatter for format time of songs */
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    /** constant variable */
    private final int FAST_FORWARD_TIME = 15000;
    private final int REWIND_TIME = 5000;

    //region Media Player Component
    /** Media Player Component declaration */

    /** Pause button for stopping currently song video*/
    private ImageButton mPauseButton;
    /** go fast ahead with more times */
    private ImageButton mFastForwardButton;
    /** go back with more time */
    private ImageButton mRewindButton;
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

    //region state variable to know should show/turn off control of Media Player
    /** state variable */
    boolean isPauseFunction = true;
    boolean isFullScreenFunction = true;
    boolean isFastForwardFunction = true;
    boolean isReWindFunction = true;
    boolean isNextFunction = true;
    boolean isPreviousFunction = true;
    boolean isSeekBarFunction = true;
    //endregion

    /** Use mHandler for processing on Multi thread */
    private Handler mHandler = new MessageHandler(this);

    /** set components for Media Player */
    public void setPauseButton(boolean state) {
        isPauseFunction = state;
    }
    public void setFullscreenButton(boolean state) {
        isFullScreenFunction = state;
    }

    public void setFastForwardButton(boolean state) {
        isFastForwardFunction = state;
    }

    public void setRewindButton(boolean state) {
        isReWindFunction = state;
    }

    public void setNextFunction(boolean state) {
        isNextFunction = state;
    }

    public void setPreviousButton(boolean state) {
        isPreviousFunction = state;
    }

    public void setSeekBarButton(boolean state) {
        isSeekBarFunction = state;
    }


    //region Construct all components of class
    //////////////////////////////////////////////////////////////////////
    //////////////////// CONSTRUCTOR METHOD //////////////////////////////

    /** constructor assign rootView can parse Media Player elements */
    public MusicPlayerController(View rootView) {
        this.mRootView = rootView;
        mUseFastForward = true;
        // parse element for this Music Player Control
        initializeComponents();
        show(sDefaultTimeout);
    }

    /** set which class will control behaviour of currently MediaPlayer */
    public void setMediaPlayer(IMediaPlayerControl player) {
        this.player = player;
        updatePausePlay();
        updateFullScreen();
    }

    /**
     * Initialize Component Action
     * Add Listener for those component
     * those listeners objects have been declare below at Listener Scope
     */
    private void initializeComponents() {
        mPauseButton = (ImageButton) mRootView.findViewById(R.id.pause);
        if (mPauseButton != null) {
            LogUtils.LOGD(TAG, "Pause Button: " + mPauseButton);
            isPauseFunction = true;
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        } else {
            isPauseFunction = false;
        }

        fullscreenButton = (ImageButton) mRootView.findViewById(R.id.fullscreen);
        if (fullscreenButton != null) {
            LogUtils.LOGD(TAG, "Fullscreen Button: " + fullscreenButton);
            isFullScreenFunction = true;
            fullscreenButton.requestFocus();
            fullscreenButton.setOnClickListener(mFullscreenListener);
        } else {
            isFullScreenFunction = false;
        }

        mFastForwardButton = (ImageButton) mRootView.findViewById(R.id.ffwd);
        if (mFastForwardButton != null) {
            LogUtils.LOGD(TAG, "FastForward Button: " + mFastForwardButton);
            isFastForwardFunction = true;
            mFastForwardButton.setOnClickListener(mFastForwardListener);
            mFastForwardButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
        } else {
            isFastForwardFunction = false;
        }

        mRewindButton = (ImageButton) mRootView.findViewById(R.id.rew);
        if (mRewindButton != null) {
            LogUtils.LOGD(TAG, "Rewind Button: " + mRewindButton);
            isReWindFunction = true;
            mRewindButton.setOnClickListener(mRewindListener);
            mRewindButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
        } else {
            isReWindFunction = false;
        }

        // By default these are hidden. They will be enabled when setupPreviousNextListeners() is called
        mNextButton = (ImageButton) mRootView.findViewById(R.id.next);
        if (mNextButton != null && isNextSongFunction) {
            LogUtils.LOGD(TAG, "Next Button: " + mNextButton);
            isNextFunction = true;
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            isNextFunction = false;
        }

        prevButton = (ImageButton) mRootView.findViewById(R.id.prev);
        if (prevButton != null && isNextSongFunction) {
            LogUtils.LOGD(TAG, "Previous Button: " + prevButton);
            isPreviousFunction = true;
            prevButton.setVisibility(View.VISIBLE);
        } else {
            isPreviousFunction = false;
        }

        mProgress = (ProgressBar) mRootView.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            LogUtils.LOGD(TAG, "Progress Button: " + mProgress);
            isSeekBarFunction = true;
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) mRootView.findViewById(R.id.time);
        mCurrentTime = (TextView) mRootView.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        setupPreviousNextListeners();
    }
    //endregion

    //region Setup Component such as initialize object, set listeners ....
    ////////////////////////////////////////////////////////////
    //////////////// SETUP COMPONENT METHOD ///////////////////

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     * Class implements this interface must declare explicitly enable or not
     */
    private void disableUnsupportedButtons() {
        if (player == null) return;
        if (mPauseButton != null && !player.canPause()) {
            mPauseButton.setEnabled(false);
        }
        if (mRewindButton != null && !player.canSeekBackward()) {
            mRewindButton.setEnabled(false);
        }
        if (mFastForwardButton != null && !player.canSeekForward()) {
            mFastForwardButton.setEnabled(false);
        }
    }

    /** Set Enable for Media Player Control */
    private void enabledSupportedButtons(boolean enabled) {
        if (isPauseFunction) {
            mPauseButton.setEnabled(enabled);
        }
        if (isFastForwardFunction) {
            mFastForwardButton.setEnabled(enabled);
        }
        if (isReWindFunction) {
            mRewindButton.setEnabled(enabled);
        }
        if (isNextFunction) {
            mNextButton.setEnabled(enabled && mNextListener != null);
        }
        if (isPreviousFunction) {
            prevButton.setEnabled(enabled && mPrevListener != null);
        }
        if (isSeekBarFunction) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
    }

    /** Stop or Pause Player. base on current state of player. And redrawn UI */
    private void setupPauseResume() {
        if (player == null) return;
        if (player.isPlaying()) {
            LogUtils.LOGE(TAG, "Pause Media Player");
            player.pause();
        } else {
            LogUtils.LOGE(TAG, "Start Media Player");
            player.start();
        }
        updatePausePlay();
    }

    /**
     * Make Current Media Player fullscreen or not.
     * by calling player object (class that implement {VideoControllerView} Interface
     */
    private void setupToggleFullscreen() {
        if (player == null) return;
        // call fullscreen from Callback
        player.toggleFullScreen();
        updateFullScreen();
    }

    /**
     * Set Listener for Previous Button and Next Button
     * public method setupPreviousNextListeners() will call this private method
     * this method will set listener object for button (already define in listener object scope)
     */
    private void setupPreviousNextListeners() {
        if (isNextFunction) {
            mNextButton.setOnClickListener(mNextListener);
            mNextButton.setEnabled(mNextListener != null);
        }

        if (isPreviousFunction) {
            prevButton.setOnClickListener(mPrevListener);
            prevButton.setEnabled(mPrevListener != null);
        }
    }

    /**
     * Set action for those two buttons. After set. this button will active.
     * By default, it will not active
     */
    public void setupPreviousNextListeners(View.OnClickListener next, View.OnClickListener prev) {
        mNextListener = next;
        mPrevListener = prev;
        isNextSongFunction = true;

        if (mRootView != null) {
            // set action and enable for those two buttons
            setupPreviousNextListeners();

            // set visible for those two buttons
            // so users can see this buttons
            if (mNextButton != null) {
                mNextButton.setVisibility(View.VISIBLE);
            }
            if (prevButton != null) {
                prevButton.setVisibility(View.VISIBLE);
            }
        }
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //endregion


    //region In-playing Setup
    ///////////////////////////////////////////////////////////////
    ////////////////// IN-PLAYING SETUP ///////////////////////////
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
     * @param timeout The timeout in milliseconds. Use 0 to show the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing) {
            setProgress();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }
            disableUnsupportedButtons();

            // TODO thinking again here
            /*FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP
            );

            mAnchor.addView(this, tlp);*/
            mShowing = true;
            throw new UnsupportedOperationException();
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

    /** Remove the controller from the screen. */
    public void hide() {
       throw new UnsupportedOperationException();
       /* if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;*/
    }

    /**
     * Update image for Play Button Icon
     * if player is playing, display stop button
     * if player is stopped, display playing button
     */
    public void updatePausePlay() {
        if (mRootView == null || player == null || (!isPauseFunction)) return;
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
        if (mRootView == null || player == null || (!isFullScreenFunction)) return;
        if (player.isFullScreen()) {
            fullscreenButton.setImageResource(R.drawable.ic_media_fullscreen_shrink);
        } else {
            fullscreenButton.setImageResource(R.drawable.ic_media_fullscreen_stretch);
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    //endregion

    //region Helper Method
    /////////////////////////////////////////////////////////////////////////////
    /////////////////////////// HELPER METHOD //////////////////////////////////

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
    public int setProgress() {
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
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //endregion

    //region LISTENER OBJECT
    /////////////////////////////////////////////////////////////////////
    ////////////// DECLARE LISTENER OBJECT FOR COMPONENT ////////////////
    ////////////////// OF MEDIA PLAYER CONTROL /////////////////////////

    /**
     * Declare Listener Object for All Components. Component just register here for listener
     * Include: FastForward FastPrevious Pause FullScreen OnSeekChange
     */

    /** Listener for FastForward Button */
    private View.OnClickListener mFastForwardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }

            int pos = player.getCurrentPosition();
            pos += FAST_FORWARD_TIME; // milliseconds
            // set SeekBar to this point
            player.seekTo(pos);
            // set progress of current video to this point
            setProgress();
            // re-drawn player
            show(sDefaultTimeout);
        }
    };

    /** Set Listener for Previous Forward Button */
    private View.OnClickListener mRewindListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (player == null) {
                return;
            }

            int pos = player.getCurrentPosition();
            pos -= REWIND_TIME; // milliseconds
            // set player SeekBar to this point
            player.seekTo(pos);
            // Change music/video to this point
            setProgress();

            // redrawn
            show(sDefaultTimeout);
        }
    };

    /** Set Listener for Pause / Play Button */
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            LogUtils.LOGE(TAG, "Pause/Play Button is pressed");
            setupPauseResume();
            show(sDefaultTimeout);
        }
    };

    /** Set Listener for FullScreen Button */
    private View.OnClickListener mFullscreenListener = new View.OnClickListener() {
        public void onClick(View v) {
            setupToggleFullscreen();
            show(sDefaultTimeout);
        }
    };

    /**
     * There are two scenarios that can trigger the seekbar listener to trigger:
     The first is the user using the touch to adjust the position of the
     seekbar's thumb. In this case onStartTrackingTouch is called followed by
     a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
     We're setting the field "mDragging" to true for the duration of the dragging
     session to avoid jumps in the position in case of ongoing playback.

     The second scenario involves the user operating the scroll ball, in this
     case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
     we will simply apply the updated position without suspending regular updates.
     */
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
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
            if (player == null) return;

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

    /** Class uses this View must implement this interface to decide action of this Media Player */
    public interface IMediaPlayerControl {
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
     * Using Weak Reference for avoiding Memory Leak
     */
    private static class MessageHandler extends Handler {
        private final WeakReference<MusicPlayerController> mView;

        MessageHandler(MusicPlayerController view) {
            mView = new WeakReference<MusicPlayerController>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicPlayerController view = mView.get();
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
