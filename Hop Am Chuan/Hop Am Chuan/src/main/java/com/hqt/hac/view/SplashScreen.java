package com.hqt.hac.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hqt.hac.config.Config;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.helper.service.SyncService;
import com.hqt.hac.helper.service.WakefulIntentService;
import com.hqt.hac.helper.task.AsyncActivity;
import com.hqt.hac.helper.widget.SongListRightMenuHandler;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.dal.FavoriteDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistSongDataAccessLayer;
import com.hqt.hac.model.json.DBVersion;
import com.hqt.hac.model.json.JsonPlaylist;
import com.hqt.hac.provider.HopAmChuanDatabase;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.ResourceUtils;
import com.hqt.hac.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
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
    /** variable control when new update is available **/
    AtomicBoolean isUpdateAvailable = new AtomicBoolean(false);
    /** Handler to switch to another activity */
    Handler mHandler;

    /** **/
    private DBVersion version;
    private AutoUpdateHandler updateHandler;
    private AutoSyncHandler syncHandler;
    private TextView statusTV;

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


        statusTV = (TextView) findViewById(R.id.status);

        // If auto update is on, then display the text box.
        if (PrefStore.isAutoUpdate()) {
            statusTV.setVisibility(View.VISIBLE);
        }

        // if already finish work after seconds. change to main screen
        // if not. wait until it finish work
        Thread t  = new Thread(new Runnable() {
            @Override
            public void run() {
                // sleep for two second
                NetworkUtils.stimulateNetwork(SPLASH_TIME_OUT);
                isSplashTimeOut.set(true);
                // If long work have finished and no update is available
                if (isFinishWork.get() && !isUpdateAvailable.get()) {
                    mHandler.sendMessage(mHandler.obtainMessage());
                }
            }
        });
        t.start();

        updateHandler = new AutoUpdateHandler();
        syncHandler = new AutoSyncHandler();

        runningLongTask();
    }

    /** loading long work before come to main screen */
    private void onLongWork() {
        /** Check is first use. if true. copy database */
        if (PrefStore.isFirstRun() || (!ResourceUtils.isDatabaseFileExist())) {
            LOGE(TAG, "First Running Database. Create new Database");
            publishProgress(0);
            // copy database
            NetworkUtils.stimulateNetwork(Config.MAX_LONG_WORK_TIMEOUT);
            SQLiteDatabase helper = new HopAmChuanDatabase(getApplicationContext()).getReadableDatabase();
            // mat trinh
            if (helper != null) {
                helper.close();
                PrefStore.setDeployedApp();
            }
        } else {
            LOGE(TAG, "Second Running Database. Nothing Change");

            // Check update
            setUpAutoUpdate();
        }

        /** load all playlist here for performance */
        playlistList = (ArrayList)PlaylistDataAccessLayer.getAllPlayLists(getApplicationContext());

        // LOGE(TAG, "Alarm Service");
        // WakefulIntentService.scheduleAlarms(new SyncService.SyncServiceAlarm(), this, false);

    }

    @Override
    public void onBackPressed() {
        // Press back button to skip splash screen
        startActivity();
        finish();
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
        statusTV.setVisibility(View.VISIBLE);
    }

    @Override
    public Integer doInBackground() {
        onLongWork();
        return 0;
    }

    @Override
    public void onProgressUpdate(Integer... values) {
        statusTV.setText(R.string.first_running);
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
        // If splash time out is over and no update available
        if (isSplashTimeOut.get() && !isUpdateAvailable.get()) {
            startActivity();
            finish();
        }
    }


    private void setUpAutoUpdate() {
        if (PrefStore.isAutoUpdate()) {
            version = APIUtils.getLatestDatabaseVersion(PrefStore.getLatestVersion());
            // no update need
            if (version != null && version.no != PrefStore.getLatestVersion()) {
                // New Version Available
                isUpdateAvailable.set(true);
                updateHandler.sendMessage(updateHandler.obtainMessage());
            }
        }

        if (PrefStore.isAutoSync() && HacUtils.isLoggedIn()) {
            syncHandler.sendMessage(syncHandler.obtainMessage());
        }
    }

    //region Auto Update Handlers
    /**
     * Handle Class for new version (auto update)
     */
    private class AutoUpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (version == null) return;
            try {
                String message = String.format(getString(R.string.auto_update_message), version.numbers);
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setTitle(getString(R.string.auto_update))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(SplashScreen.this, SettingActivity.class);
                                intent.putExtra(Config.BUNDLE_AUTO_UPDATE_SONG, true);
                                startActivity(intent);
                                if (isUpdateAvailable.get()) {
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (isUpdateAvailable.get()) {
                                    startActivity();
                                    finish();
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                // User have press Back button to skip updating
                // In case of the activity is reset or closed
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle Class for sync songs
     */
    private class AutoSyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (!HacUtils.isLoggedIn()) return;
                Context mAppContext = getApplicationContext();
                if (mAppContext == null) return;

                String username = PrefStore.getLoginUsername();
                String password = PrefStore.getLoginPassword();
                boolean res;

                List<Playlist> oldPlaylists = PlaylistDataAccessLayer.getAllPlayLists(mAppContext);
                List<JsonPlaylist> jsonPlaylists = JsonPlaylist.convert(oldPlaylists, mAppContext);
                List<Playlist> newPlaylists = APIUtils.syncPlaylist(username, password, jsonPlaylists);

                if (newPlaylists != null) {
                    // delete all playlist in system
                    PlaylistDataAccessLayer.removeAllPlaylists(mAppContext);

                    // insert all song of its playlist to database
                    for (Playlist playlist : newPlaylists) {
                        // insert playlist
                        PlaylistDataAccessLayer.insertPlaylist(mAppContext, playlist);
                        // insert songs of playlist
                        List<Integer> ids = playlist.getAllSongIds(mAppContext);
                        res = PlaylistSongDataAccessLayer.insertPlaylist_Song(mAppContext, playlist.playlistId, ids);
                    }
                }
                int[] favorite = FavoriteDataAccessLayer.getAllFavoriteSongIds(mAppContext);
                List<Integer> newFavorite = APIUtils.syncFavorite(username, password, favorite);
                res = FavoriteDataAccessLayer.syncFavorites(mAppContext, newFavorite);
                SongListRightMenuHandler.updateNavDrawerPlaylistList(
                        PlaylistDataAccessLayer.getAllPlayLists(mAppContext));

                if (res) {
                    Toast.makeText(mAppContext, getString(R.string.sync_success), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mAppContext, getString(R.string.auto_sync_error), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                // User have press Back button to skip updating
                // In case of the activity is reset or closed
                e.printStackTrace();
            }
        }
    }
    //endregion
}