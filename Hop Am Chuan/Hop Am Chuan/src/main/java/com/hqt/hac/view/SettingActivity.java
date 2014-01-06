package com.hqt.hac.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
    Button updateSongBtn;
    Button syncSongBtn;
    CheckBox autoUpdateSongChkbox;
    CheckBox autoSyncSongChkbox;

    Context mAppContext;

    /** method to know which type of update */
    int method = 0;

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

        setUpAccountInfo();
        setUpSync();
        setUpSettingLanguage();
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
        languageSettingTxt.setOnClickListener(new View.OnClickListener() {
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
        HacUtils.logout(getApplicationContext());
        // Reload the mActivity
        loadAccountInfo();
    }

    private void loadAccountInfo() {
        // Account info

        TextView txtName = (TextView) findViewById(R.id.name);
        TextView txtMail = (TextView) findViewById(R.id.mail);
        ImageView imgAvatar = (ImageView) findViewById(R.id.imageView);

        txtName.setText(PrefStore.getLoginUsername());
        txtMail.setText(PrefStore.getEmail());
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
        // set value and action for widget
        currentVersionTxt.setText(getString(R.string.current_version) + " " + PrefStore.getLatestVersion());

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
            AlertDialog dialog = DialogUtils.showAlertDialog(SettingActivity.this, "Network Problem", "Check Your Wifi or 3G Network Again");
            dialog.show();
            return;
        }
        this.method = method;
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
            case 0 :
                return updateSongTask();
            case 1:
                return syncSongTask();
            case 2:
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
                dialog.setMessage("Checking Version ...");
                break;
            case STATUS_CODE.DOWNLOADING:
                dialog.setMessage("Downloading ...");
                break;
            case STATUS_CODE.UPDATING:
                dialog.setMessage("Updating ...");
                break;
            case STATUS_CODE.SYNC_FAVORITE:
                dialog.setMessage("Sync Favorite ...");
                break;
            case STATUS_CODE.SYNC_PLAYLIST:
                dialog.setMessage("Sync Playlist ...");
                break;
            default:
                throw new UnsupportedOperationException();
        }
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
                AlertDialog dialog = DialogUtils.showAlertDialog(this, "Notify", "Already newest version");
                dialog.show();
                break;
            }

            // download or parse fail
            case STATUS_CODE.NETWORK_ERROR: {
                AlertDialog dialog = DialogUtils.showAlertDialog(this, "Error", "Network Error. Try again");
                dialog.show();
                break;
            }

            // update to database fail
            case STATUS_CODE.SYSTEM_ERROR: {
                AlertDialog dialog = DialogUtils.showAlertDialog(this, "Error", "System Fail. Restart Application");
                dialog.show();
                break;
            }

            // successfully
            case STATUS_CODE.SUCCESS: {
                // notify to user
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notify")
                        .setMessage("Update Finish")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
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
            PrefStore.setLatestVersion(version.no);
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
    //endregion
}
