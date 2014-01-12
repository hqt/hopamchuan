package com.hqt.hac.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hqt.hac.config.Config;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.helper.service.SyncService;
import com.hqt.hac.helper.service.WakefulIntentService;
import com.hqt.hac.helper.task.AsyncActivity;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.ResourceUtils;
import com.hqt.hac.utils.UIUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SplashScreen extends AsyncActivity {

    private static final String TAG = makeLogTag(SplashScreen.class);

    // Splash screen timer
    private static int SPLASH_TIME_OUT = Config.SPLASH_SCREEN_TIMEOUT;
    /** pre-load playlist list */
    ArrayList<Playlist> playlistList;
    /** variable control when finish time wait */
    AtomicBoolean isSplashTimeOut = new AtomicBoolean(false);
    /** variable control when all work is finish */
    AtomicBoolean isFinishWork = new AtomicBoolean(false);
    /** Handler to switch to another activity */
    Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Language setting
        UIUtils.setLanguage(getBaseContext());

        setContentView(R.layout.activity_splash_screen);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                startActivity();
                finish();
            }
        };
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

        runningLongTask();
    }

    ProgressDialog dialog;
    /** loading long work before come to main screen */
    private void onLongWork() {
        dialog.setMessage(getString(R.string.first_running));
        dialog.setCancelable(false);
        /** Check is first use. if true. copy database */
        if (PrefStore.isFirstRun() || (!ResourceUtils.isDatabaseFileExist())) {
            LOGE(TAG, "First Running Database. Create new Database");
            publishProgress(0);
            // copy database
            NetworkUtils.stimulateNetwork(5000);
            SQLiteDatabase helper = new HopAmChuanDatabase(getApplicationContext()).getReadableDatabase();
            // mat trinh
            if (helper != null) {
                helper.close();
                PrefStore.setDeployedApp();
            }
        } else {
            LOGE(TAG, "Second Running Database. Nothing Change");
        }

        /** load all playlist here for performance */
        playlistList = (ArrayList)PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());
        dialog.dismiss();

        LOGE(TAG, "Alarm Service");
        WakefulIntentService.scheduleAlarms(new SyncService.SyncServiceAlarm(), this, false);

    }

    @Override
    public void onBackPressed() {
        // Yes, nothing here. Because we don't want user to exit our app in splash screen.
    }

    private void startActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.putParcelableArrayListExtra("playlistList", playlistList);
        startActivity(intent);
        finish();
    }

    ////////////////////////////////////////////////////////////
    ////////////// Background running thread ///////////////////

    /** this is a awful task that user should wait to finish */

    @Override
    public void onPreExecute() {
        dialog = new ProgressDialog(this);
    }

    @Override
    public Integer doInBackground() {
        onLongWork();
        return 0;
    }

    @Override
    public void onProgressUpdate(Integer... values) {
        dialog.show();

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onPostExecute(int status) {
        LOGE(TAG, "Finish Work");
        dialog.dismiss();
        // flag to mark already finish work
        isFinishWork.set(true);
        // after long work. if flash still not come. wait for him
        if (isSplashTimeOut.get()) {
           startActivity();
        }
    }

}