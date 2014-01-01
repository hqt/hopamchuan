package com.hqt.hac.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dao.PlaylistDataAccessLayer;
import com.hqt.hac.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SplashScreen extends Activity {

    private static final String TAG = makeLogTag(SplashScreen.class);

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2;
    /** pre-load playlist list */
    ArrayList<Playlist> playlistList;
    /** variable control when finish time wait */
    AtomicBoolean isSplashTimeOut = new AtomicBoolean(false);
    /** variable control when all work is finish */
    AtomicBoolean isFinishWork = new AtomicBoolean(false);
    /** Handler to switch to another activity */
    SwitchActivityHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mHandler = new SwitchActivityHandler();

        // if already finish work after seconds. change to main screen
        // if not. wait until it finish work
        Thread t  = new Thread(new Runnable() {
            @Override
            public void run() {
                // sleep for two second
                NetworkUtils.stimulateNetwork(SPLASH_TIME_OUT);
                isSplashTimeOut.set(true);
                if (isFinishWork.get()) {
                    mHandler.sendMessage(mHandler.obtainMessage());
                }
            }
        });
        t.start();

        onLongWork();
        // after long work. if flash still not come. wait for him
        if (isSplashTimeOut.get()) {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity();
            finish();
        }
    }

    /** loading long work before come to main screen */
    private void onLongWork() {
        /** load all playlist here */
        playlistList = (ArrayList)PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());
        isFinishWork.set(true);
    }

    private class SwitchActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            startActivity();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // Yes, nothing here. Because we don't want user to exit our app in splash screen.
    }

    private void startActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.putParcelableArrayListExtra("playlistList", playlistList);
        startActivity(intent);
    }
}