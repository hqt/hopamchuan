package com.hqt.hac.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hqt.hac.config.PrefStore;
import com.hqt.hac.helper.task.SyncSongAsyncTask;
import com.hqt.hac.helper.task.UpdateSongAsyncTask;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.UIUtils;

public class SettingActivity extends ActionBarActivity {


    /** Screen Widget */
    TextView currentVersionTxt;
    ImageButton updateSongBtn;
    ImageButton syncSongBtn;
    Button updateAllBtn;
    CheckBox autoUpdateSongChkbox;
    CheckBox autoSyncSongChkbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_setting);

        /** find id of all widget */
        currentVersionTxt = (TextView) findViewById(R.id.current_version);
        updateSongBtn = (ImageButton) findViewById(R.id.update_song_button);
        syncSongBtn = (ImageButton) findViewById(R.id.sync_song_button);
        updateAllBtn = (Button) findViewById(R.id.update_now_button);
        autoSyncSongChkbox = (CheckBox) findViewById(R.id.checkbox_auto_sync);
        autoUpdateSongChkbox = (CheckBox) findViewById(R.id.checkbox_auto_update);

        // set value and action for widget
        currentVersionTxt.setText(R.string.current_version + " " + PrefStore.getLatestVersion(getApplicationContext()));

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

        updateAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(2);
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

    private void updateSongs() {
        UpdateSongAsyncTask task = new UpdateSongAsyncTask(this);
        task.execute();
    }

    private void syncData() {
        SyncSongAsyncTask task = new SyncSongAsyncTask(this);
        task.execute();
    }

    private void update(int method) {
        // check network
        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
            AlertDialog dialog = UIUtils.showAlertDialog(SettingActivity.this, "Network Problem", "Check Your Wifi or 3G Network Again");
            dialog.show();
            return;
        }

        switch (method) {
            case 0 :
                updateSongs();
                break;
            case 1:
                syncData();
                break;
            case 2:
                updateSongs();
                syncData();
        }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
