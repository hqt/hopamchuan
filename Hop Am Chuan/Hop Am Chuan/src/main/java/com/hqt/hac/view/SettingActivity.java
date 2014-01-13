package com.hqt.hac.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.hqt.hac.config.Config;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.helper.task.AsyncActivity;
import com.hqt.hac.model.Playlist;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.FavoriteDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistSongDataAccessLayer;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.model.json.DBVersion;
import com.hqt.hac.model.json.JsonPlaylist;
import com.hqt.hac.provider.SearchRecentProvider;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.EncodingUtils;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.ParserUtils;
import com.hqt.hac.utils.PrefStoreUtils;
import com.hqt.hac.utils.ResourceUtils;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.popup.ProfilePopup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hqt.hac.utils.LogUtils.LOGE;

public class SettingActivity extends AsyncActivity {


    /** Screen Widget */
    TextView currentVersionTxt;
    TextView currentDatabaseSongTxt;
    TextView languageSettingTxt;
    TextView currentLanguageTxt;
    TextView appDetailTxt;
    TextView clearSearchHistoryTxt;
    Button updateSongBtn;
    Button syncSongBtn;
    CheckBox autoUpdateSongChkbox;
    CheckBox autoSyncSongChkbox;
    View languageView;

    Context mAppContext;

    /** method to know which type of update */
    int method = METHOD_CODE.UPDATE_SONG;

    /** Variable to know how many song have been updated **/
    private int updatedSongs = 0;

    /** Variable to access across threads **/
    DBVersion version;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Language setting
        UIUtils.setLanguage(getBaseContext());

        setContentView(R.layout.fragment_setting);

        mAppContext = getApplicationContext();

        /** find id of all widget */
        currentVersionTxt = (TextView) findViewById(R.id.current_version);
        currentDatabaseSongTxt = (TextView) findViewById(R.id.current_database_song);
        updateSongBtn = (Button) findViewById(R.id.update_song_button);
        syncSongBtn = (Button) findViewById(R.id.sync_song_button);
        autoSyncSongChkbox = (CheckBox) findViewById(R.id.checkbox_auto_sync);
        autoUpdateSongChkbox = (CheckBox) findViewById(R.id.checkbox_auto_update);
        languageSettingTxt = (TextView) findViewById(R.id.language_setting_txt);
        currentLanguageTxt = (TextView) findViewById(R.id.current_language_txt);
        languageView = findViewById(R.id.linear_layout_language_setting);
        appDetailTxt = (TextView) findViewById(R.id.app_detail);
        clearSearchHistoryTxt = (TextView) findViewById(R.id.clear_cache_data_text_view);

        clearSearchHistoryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

        appDetailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SecondarySettingActivity.class);
                startActivity(intent);
            }
        });

        setUpAccountInfo();
        setUpSync();
        setUpSettingLanguage();

        // Handle auto update request
        if (getIntent().getBooleanExtra(Config.BUNDLE_AUTO_UPDATE_SONG, false)) {
            // Start download songs;
            update(0);
        }

    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.notif_title_info))
                .setMessage(getString(R.string.clear_history_msg))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(SettingActivity.this,
                                SearchRecentProvider.AUTHORITY, SearchRecentProvider.MODE);
                        suggestions.clearHistory();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //region Language Setting Function
    int currentLanguageId = 0;
    String currentTitle = "";
    CharSequence[] currentOption;
    String currentNotifyText;
    private void setUpSettingLanguage() {
        if (PrefStore.getSystemLanguage().equals(Config.LANGUAGE_VIETNAMESE)) {
            currentLanguageId = 0;
        } else {
            currentLanguageId = 1;
        }
       settingOptionString();
        languageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });
    }

    private void settingOptionString() {
        if (currentLanguageId == 0) {
            currentLanguageId = 0;
            currentTitle = "Ngôn Ngữ";
            currentOption = new CharSequence[]{"Tiếng Việt", "English", "Mặc Định"};
            currentNotifyText = "Restart Hợp Âm Chuẩn app to apply changes";
        } else {
            currentLanguageId = 1;
            currentTitle = "Language";
            currentOption = new CharSequence[]{"Tiếng Việt", "English", "Default"};
            currentNotifyText = "Khởi động lại ứng dụng Hợp Âm Chuẩn để thay đổi có hiệu lực";
        }
        languageSettingTxt.setText(currentTitle);
        currentLanguageTxt.setText(currentOption[currentLanguageId]);
    }

    private void showListDialog() {
        final int[] changeLanguageId = {0};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(currentTitle)
                .setItems(currentOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case 0:
                                PrefStore.setSystemLanguage(Config.LANGUAGE_VIETNAMESE);
                                changeLanguageId[0] = 0;
                                break;
                            case 1:
                                PrefStore.setSystemLanguage(Config.LANGUAGE_ENGLISH);
                                changeLanguageId[0] = 1;
                                break;
                            case 2:
                                PrefStore.setSystemLanguage(Config.LANGUAGE_DEFAULT);
                                changeLanguageId[0] = 1;
                                break;
                        }
                        if (changeLanguageId[0] != currentLanguageId) {
                            Toast.makeText(getBaseContext(), currentNotifyText, Toast.LENGTH_SHORT).show();
                            currentLanguageId = changeLanguageId[0];
                            settingOptionString();
                        }
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }
    //endregion

    //region Account Function

    private void setUpAccountInfo() {
        // Account info
        loadAccountInfo();

        // Login / Logout Buttons
        TextView btnLogout = (TextView) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        RelativeLayout loginBtn = (RelativeLayout) findViewById(R.id.loginBtn);
        final Activity finalActivity = this;
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HacUtils.isLoggedIn()) {
                    // start Login Activity
                    Intent intent = new Intent(finalActivity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Start logout mActivity or popup here.
                    ProfilePopup profilePopup = new ProfilePopup(finalActivity);
                    profilePopup.show();
                }
            }
        });
    }

    private void logout() {
        HacUtils.logout(this, new HacUtils.AfterLogoutDelegate() {
            @Override
            public void onAfterLogout() {
                // Reload the mActivity
                loadAccountInfo();
            }
        });
    }

    private void loadAccountInfo() {
        // Account info

        TextView txtName = (TextView) findViewById(R.id.name);
        TextView txtMail = (TextView) findViewById(R.id.mail);
        ImageView imgAvatar = (ImageView) findViewById(R.id.imageView);
        String username = PrefStore.getLoginUsername();
        String email = PrefStore.getEmail();

        if (username.isEmpty()) username = getString(R.string.login_account);
        if (email.isEmpty()) email = getString(R.string.login_account_description);

        txtName.setText(username);
        txtMail.setText(email);
        Bitmap imageAvatar = EncodingUtils.decodeByteToBitmap(PrefStore.getUserImage());

        if (imageAvatar != null) {
            imgAvatar.setImageBitmap(imageAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.default_avatar);
        }
    }
    //endregion

    //region Sync Function
    private void setUpSync() {

        String lastedDate = PrefStore.getLastedUpdateDate();

        if (lastedDate.isEmpty()) lastedDate = getString(R.string.default_last_update);

        // set value and action for widget
        currentVersionTxt.setText(getString(R.string.current_version) + " " + lastedDate);
        currentDatabaseSongTxt.setText(getString(R.string.current_database_song)
                + " " + SongDataAccessLayer.getSongCount());

        CheckBox autoUpdateChkBox = (CheckBox) findViewById(R.id.checkbox_auto_update);
        CheckBox autoSyncChkBox = (CheckBox) findViewById(R.id.checkbox_auto_sync);
        CheckBox connectionTypeChkBox = (CheckBox) findViewById(R.id.checkbox_network_type);

        autoUpdateChkBox.setChecked(PrefStore.isAutoUpdate());
        autoSyncChkBox.setChecked(PrefStore.isAutoSync());
        autoSyncChkBox.setChecked(PrefStore.isMobileNetwork());

        autoUpdateChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefStore.setAutoUpdate(isChecked);
            }
        });

        autoSyncChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefStore.setAutoSyc(isChecked);
            }
        });

        connectionTypeChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefStore.setCanMobileNetwork(isChecked);
            }
        });

        updateSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(0);
            }
        });

        syncSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(1);
            }
        });

        autoSyncSongChkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefStore.setAutoUpdate(isChecked);
            }
        });

        autoUpdateSongChkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefStore.setAutoSyc(isChecked);
            }
        });
    }

    private void update(int method) {
        // check network
        if (!NetworkUtils.isDeviceNetworkConnected()) {
            AlertDialog dialog = DialogUtils.showAlertDialog(SettingActivity.this,
                    getString(R.string.notif_title_error),
                    getString(R.string.no_connection));
            dialog.show();
            return;
        }

        // If the user wanted to sync songs, then check if user have logged in
        if (method >= METHOD_CODE.SYNC_SONG) {
            if (!HacUtils.isLoggedIn()) {
                AlertDialog dialog = DialogUtils.showAlertDialog(SettingActivity.this,
                        getString(R.string.notif_title_error),
                        getString(R.string.need_login));
                dialog.show();
                return;
            }
        }

        this.method = method;

        // Reset new songs count status to zero
        updatedSongs = 0;
        // just call this method. all magic things will be happened
        runningLongTask();
    }

    /////////////////////////////////////////////////////////////////
    ////////////////// ASYNCTASK METHOD CALLBACK ///////////////////
    @Override
    public void onPreExecute() {

    }

    @Override
    public Integer doInBackground() {
        switch (method) {
            case METHOD_CODE.UPDATE_SONG :
                return updateSongTask();
            case METHOD_CODE.SYNC_SONG:
                return syncSongTask();
            case METHOD_CODE.UPDATE_AND_SYNC_SONG:
                int statusCode = updateSongTask();
                if (statusCode != STATUS_CODE.SUCCESS) return statusCode;
                return syncSongTask();
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onProgressUpdate(Integer... values) {
        switch (values[0]) {
            case STATUS_CODE.STATE_CHECKING_VERSION:
                dialog.setMessage(getString(R.string.checking_version));
                break;
            case STATUS_CODE.STATE_DOWNLOADING:
                dialog.setMessage(getString(R.string.downloading));
                break;
            case STATUS_CODE.STATE_UPDATING:
                dialog.setMessage(getString(R.string.updating_remote_song_to_local));
                break;
            case STATUS_CODE.STATE_SYNC_FAVORITE:
                dialog.setMessage(getString(R.string.synching_favorite));
                break;
            case STATUS_CODE.STATE_SYNC_PLAYLIST:
                dialog.setMessage(getString(R.string.synching_playlist));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        try {
            // To prevent accidentally close the popup.
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onPostExecute(int result) {
        dialog.dismiss();

        switch (result) {
            // latest version
            case STATUS_CODE.LATEST_VERSION: {
                AlertDialog dialog = DialogUtils.showAlertDialog(this,
                        getString(R.string.notif_title_info),
                        getString(R.string.already_lasted_version));
                dialog.show();

                resetStats();
                break;
            }

            // download or parse fail
            case STATUS_CODE.NETWORK_ERROR: {
                AlertDialog dialog = DialogUtils.showAlertDialog(this,
                        getString(R.string.notif_title_error),
                        getString(R.string.network_error));
                dialog.show();
                break;
            }

            // update to database fail
            case STATUS_CODE.SYSTEM_ERROR: {
                AlertDialog dialog = DialogUtils.showAlertDialog(this,
                        getString(R.string.notif_title_error),
                        getString(R.string.system_fail_error));
                dialog.show();
                break;
            }

            // successfully
            case STATUS_CODE.SUCCESS: {

                String newSongs = updatedSongs > 0 ? "\n" + getString(R.string.song_updated_count) + " " + updatedSongs : "";
                // notify to user
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.notif_title_info))
                        .setMessage(getString(R.string.update_susscess) + newSongs)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            }

            // new version available
            case STATUS_CODE.NEW_VERSION_AVAILABLE: {
                String mess = String.format(getString(R.string.update_new_songs),
                        (new SimpleDateFormat(Config.UPDATE_DATE_FORMAT)).format(version.date),
                        version.numbers);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.notif_title_info))
                        .setMessage(mess)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                initDownloadDialog();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LOGE("TRUNGDQ", "cancel");
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                break;
            }

            default:
                throw new UnsupportedOperationException();
        }
    }

    ////////////////////////////////////////////////////////////////
    //////////////////// UPDATE SONG TASK //////////////////////////

    private int updateSongTask() {
        // check version
        publishProgress(STATUS_CODE.STATE_CHECKING_VERSION);
        version = APIUtils.getLatestDatabaseVersion(PrefStore.getLatestVersion());
        // no update need
        if (version == null || version.no == PrefStore.getLatestVersion()) {
            if (version != null) {
                PrefStore.setLastestVersion(version.no);
                PrefStore.setLastedUpdate(version.date);
            }
            return STATUS_CODE.LATEST_VERSION;
        }
        return STATUS_CODE.NEW_VERSION_AVAILABLE;
    }

    /**
     * Update lasted version and songs number
     */
    private void resetStats() {
        String lastedDate = PrefStore.getLastedUpdateDate();

        if (lastedDate.isEmpty()) lastedDate = getString(R.string.default_last_update);

        // set value and action for widget
        currentVersionTxt.setText(getString(R.string.current_version) + " " + lastedDate);
        currentDatabaseSongTxt.setText(getString(R.string.current_database_song)
                + " " + SongDataAccessLayer.getSongCount());
    }

    ////////////////////////////////////////////////////////////////
    /////////////////// SYNC SONG TASK /////////////////////////////

    private int syncSongTask() {
        String username = PrefStore.getLoginUsername();
        String password = PrefStore.getLoginPassword();
        boolean res;

        // sync playlist
        publishProgress(STATUS_CODE.STATE_SYNC_PLAYLIST);
        List<Playlist> oldPlaylists = PlaylistDataAccessLayer.getAllPlayLists(mAppContext);
        List<JsonPlaylist> jsonPlaylists = JsonPlaylist.convert(oldPlaylists, mAppContext);
        List<Playlist> newPlaylists = APIUtils.syncPlaylist(username, password, jsonPlaylists);

        // update playlist
        publishProgress(STATUS_CODE.STATE_UPDATING);
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
                if (!res) return STATUS_CODE.SYSTEM_ERROR;
            }
        }

        // sync favorite
        publishProgress(STATUS_CODE.STATE_SYNC_FAVORITE);
        int[] favorite = FavoriteDataAccessLayer.getAllFavoriteSongIds(mAppContext);
        List<Integer> newFavorite = APIUtils.syncFavorite(username, password, favorite);

        // update favorite
        publishProgress(STATUS_CODE.STATE_UPDATING);
        res = FavoriteDataAccessLayer.syncFavorites(mAppContext, newFavorite);
        // res = FavoriteDataAccessLayer.addAllSongIdsToFavorite(mContext, newFavorite);
        if (!res) return STATUS_CODE.SYSTEM_ERROR;
        return STATUS_CODE.SUCCESS;
    }

    /////////////////////////////////////////////////////////////////
    private static class  STATUS_CODE {
        static final int LATEST_VERSION = 0;
        static final int SYSTEM_ERROR = 1;
        static final int NETWORK_ERROR = 2;
        static final int SUCCESS = 3;
        static final int NEW_VERSION_AVAILABLE = 4;
        /** status code for in-progress */
        static final int STATE_UPDATING = 4;
        static final int STATE_DOWNLOADING = 5;
        static final int STATE_CHECKING_VERSION = 6;
        static final int STATE_SYNC_PLAYLIST = 7;
        static final int STATE_SYNC_FAVORITE = 8;
        static final int STATE2_DOWNLOADING = 9;
        static final int STATE2_PROCESSING = 10;
    }

    private static class METHOD_CODE {
        static final int UPDATE_SONG = 0;
        static final int SYNC_SONG = 1;
        static final int UPDATE_AND_SYNC_SONG = 2;
    }
    //endregion

    //region Download Songs Function
    //////////////////////////////////////////////////////////////////
    // Download Async task for large updates
    //////////////////////////////////////////////////////////////////

    int downloaderStatus = STATUS_CODE.STATE2_DOWNLOADING;
    ProgressDialog mProgressDialog;
    String tmpFilePath;
    String urlParameters;
    // take CPU lock to prevent CPU from going off if the user
    // presses the power button during download
    PowerManager pm;
    PowerManager.WakeLock wl;


    private void initDownloadDialog() {
        // instantiate it within the onCreate method
        pm = (PowerManager) mAppContext.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.downloading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        tmpFilePath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Config.TEMPLATE_FILE_NAME;

        // LOGE("TRUNGDQ", "Temp file path: " + tmpFilePath);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(this);

        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadTask.cancel(true);
            }
        });
        Map<String, String> params = new HashMap<String, String>();
        params.put("from_ver", PrefStore.getLatestVersion() + "");
        urlParameters = APIUtils.generateRequestLink("", params);

        // LOGE("TRUNGDQ", "url: " + Config.SERVICE_GET_SONGS_FROM_DATE + urlParameters);
        downloadTask.execute(Config.SERVICE_GET_SONGS_FROM_DATE);
    }


    // Download Async task. Put here to easily update UI
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            downloaderStatus = STATUS_CODE.STATE2_DOWNLOADING;
            /** Download file **/
            //region Download file
            try {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Accept-Charset", "utf-8");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

                    OutputStream out = connection.getOutputStream();
                    try {
                        out.write(urlParameters.getBytes("utf-8"));
                    } finally {
                        try { out.close(); } catch (IOException logOrIgnore) {}
                    }

                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // If fileLength is not available, estimate a length for processbar
                    if (fileLength < 0) fileLength = version.numbers * Config.ESTIMATE_SIZE_PER_SONG;

                    // download the file
                    input = connection.getInputStream();
                    output = new FileOutputStream(tmpFilePath);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled())
                            return null;
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total / 1000), (int) (fileLength / 1000));
                        output.write(data, 0, count);
                    }
                } catch (EOFException e) {
                    // Empty response
                    return getString(R.string.network_error);
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    }
                    catch (IOException ignored) { }

                    if (connection != null)
                        connection.disconnect();
                }
            } catch (Exception e) {
                return e.toString();
            }
            //endregion

            /** Add data to database **/
            downloaderStatus = STATUS_CODE.STATE2_PROCESSING;
            //region Process file

            File file = new File(tmpFilePath);
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                return getString(R.string.network_error);
            }

            // Parse
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(br).getAsJsonArray();

            final int totalItemCount = version.numbers;

            ParserUtils.parseSongsFromJsonArrayNoReturn(jsonArray, new ParserUtils.ParseListItemDoneListener() {
                @Override
                public void onParseListItemDone(Object obj, int index) {
                    try {
                        SongDataAccessLayer.insertFullSongSync(mAppContext, (Song) obj);
                        publishProgress(index, totalItemCount);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            });

            // set latest version to system after all step has successfully update
            PrefStore.setLastestVersion(version.no);
            PrefStore.setLastedUpdate(version.date);
            updatedSongs = totalItemCount;

            //endregion
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wl.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(progress[1]);
            mProgressDialog.setProgress(progress[0]);
            switch (downloaderStatus) {
                case STATUS_CODE.STATE2_DOWNLOADING: {
                    mProgressDialog.setMessage(getString(R.string.downloading));
                    break;
                }
                case STATUS_CODE.STATE2_PROCESSING: {
                    mProgressDialog.setMessage(getString(R.string.processing_data));

                    // Remove cancel button.
                    // TODO: if there is too much song and use want to do other stuff while.
                    mProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                    break;
                }
                default:
            }
        }

        @Override
        protected void onPostExecute(String result) {
            wl.release();
            mProgressDialog.dismiss();
            AlertDialog alertDialog;
            if (result != null) {
                alertDialog = DialogUtils.showAlertDialog(SettingActivity.this,
                        getString(R.string.notif_title_error), result);
            } else {
                String newSongs = updatedSongs > 0 ? "\n"
                        + getString(R.string.song_updated_count)
                        + " " + updatedSongs : "";

                alertDialog = DialogUtils.showAlertDialog(SettingActivity.this,
                        getString(R.string.notif_title_info),
                        getString(R.string.update_susscess) + newSongs);
            }

            resetStats();

            alertDialog.show();

        }
    } // End class
    //endregion
}
