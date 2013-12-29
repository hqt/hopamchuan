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
    ArrayList<Playlist> playlistList;
    AtomicBoolean isSplashTimeOut = new AtomicBoolean(false);
    AtomicBoolean isFinishWork = new AtomicBoolean(false);
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
                LOGE(TAG, "Finish Wait");
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
        }
    }

    /** loading long work before come to main screen */
    private void onLongWork() {
        /** load all playlist here */
        playlistList = (ArrayList)PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());
        isFinishWork.set(true);
        LOGE(TAG, "finish work");
        if (playlistList == null) {
            LOGE(TAG, "finish work with error");
        }
    }

    private class SwitchActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            startActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.putParcelableArrayListExtra("playlistList", playlistList);
        int count = 0;
        for (int i = 0; i < playlistList.size(); i++) {
            if (playlistList.get(i) == null) {
                count++;
            }
        }
        LOGE(TAG, "Null Elements: " + count);
        startActivity(intent);
    }
}