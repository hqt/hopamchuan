package com.hqt.hac.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.*;

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
import com.hqt.hac.view.popup.ProfilePopup;

import java.util.List;

public class SettingActivity extends AsyncActivity {


    /** Screen Widget */
    TextView currentVersionTxt;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_setting);

        mAppContext = getApplicationContext();

        /** find id of all widget */
        currentVersionTxt = (TextView) findViewById(R.id.current_version);
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
            currentNotifyText = "Restart HopAmChuan app to apply change";
        } else {
            currentLanguageId = 1;
            currentTitle = "Language";
            currentOption = new CharSequence[]{"Tiếng Việt", "English", "Default"};
            currentNotifyText = "Thoát và khởi động lại ứng dụng Hợp Âm Chuẩn để thay đổi có hiệu lực";
        }
        languageSettingTxt.setText(currentTitle);
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

        CheckBox autoUpdateChkBox = (CheckBox) findViewById(R.id.checkbox_auto_update);
        CheckBox autoSyncChkBox = (CheckBox) findViewById(R.id.checkbox_auto_sync);
        CheckBox connectionTypeChkBox = (CheckBox) findViewById(R.id.checkbox_network_type);

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

            }
        });

        autoUpdateSongChkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
            case STATUS_CODE.CHECKING_VERSION:
                dialog.setMessage(getString(R.string.checking_version));
                break;
            case STATUS_CODE.DOWNLOADING:
                dialog.setMessage(getString(R.string.downloading));
                break;
            case STATUS_CODE.UPDATING:
                dialog.setMessage(getString(R.string.updating_remote_song_to_local));
                break;
            case STATUS_CODE.SYNC_FAVORITE:
                dialog.setMessage(getString(R.string.synching_favorite));
                break;
            case STATUS_CODE.SYNC_PLAYLIST:
                dialog.setMessage(getString(R.string.synching_playlist));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        // To prevent accidentally close the popup.
        dialog.setCancelable(false);
        dialog.show();
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

            default:
                throw new UnsupportedOperationException();
        }
    }

    ////////////////////////////////////////////////////////////////
    //////////////////// UPDATE SONG TASK //////////////////////////

    private int updateSongTask() {
        // check version
        publishProgress(STATUS_CODE.CHECKING_VERSION);
        DBVersion version = APIUtils.getLatestDatabaseVersion(PrefStore.getLatestVersion());
        // no update need
        if (version == null || version.no == PrefStore.getLatestVersion()) {
            return STATUS_CODE.LATEST_VERSION;
        }

        // update songs
        publishProgress(STATUS_CODE.DOWNLOADING);
        List<Song> songs = APIUtils.getAllSongsFromVersion(PrefStore.getLatestVersion());
        if (songs == null) {
            return STATUS_CODE.NETWORK_ERROR;
        }

        // save to database
        publishProgress(STATUS_CODE.UPDATING);
        boolean status = SongDataAccessLayer.insertFullSongListSync(mAppContext, songs);
        if (!status) return STATUS_CODE.SYSTEM_ERROR;
        else {
            // set latest version to system after all step has successfully update
            PrefStore.setLastestVersion(version.no);
            PrefStore.setLastedUpdate(version.date);
            updatedSongs = songs.size();
            return STATUS_CODE.SUCCESS;
        }
    }

    ////////////////////////////////////////////////////////////////
    /////////////////// SYNC SONG TASK /////////////////////////////

    private int syncSongTask() {
        String username = PrefStore.getLoginUsername();
        String password = PrefStore.getLoginPassword();
        boolean res;

        // sync playlist
        publishProgress(STATUS_CODE.SYNC_PLAYLIST);
        List<Playlist> oldPlaylists = PlaylistDataAccessLayer.getAllPlayLists(mAppContext);
        List<JsonPlaylist> jsonPlaylists = JsonPlaylist.convert(oldPlaylists, mAppContext);
        List<Playlist> newPlaylists = APIUtils.syncPlaylist(username, password, jsonPlaylists);

        // update playlist
        publishProgress(STATUS_CODE.UPDATING);
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
        publishProgress(STATUS_CODE.SYNC_FAVORITE);
        int[] favorite = FavoriteDataAccessLayer.getAllFavoriteSongIds(mAppContext);
        List<Integer> newFavorite = APIUtils.syncFavorite(username, password, favorite);

        // update favorite
        publishProgress(STATUS_CODE.UPDATING);
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
        /** status code for in-progress */
        static final int UPDATING = 4;
        static final int DOWNLOADING = 5;
        static final int CHECKING_VERSION = 6;
        static final int SYNC_PLAYLIST = 7;
        static final int SYNC_FAVORITE  = 8;
    }

    private static class METHOD_CODE {
        static final int UPDATE_SONG = 0;
        static final int SYNC_SONG = 1;
        static final int UPDATE_AND_SYNC_SONG = 2;
    }
    //endregion
}
