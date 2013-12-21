package com.hqt.hac.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.hqt.hac.config.PrefStore;

public class SettingActivity extends Activity {

    /** Screen Widget */
    TextView currentVersionTxt;
    Button updateSongBtn;
    Button syncSongBtn;
    Button updateAllBtn;
    CheckBox autoUpdateSongChkbox;
    CheckBox autoSyncSongChkbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** find id of all widget */
        currentVersionTxt = (TextView) findViewById(R.id.current_version);
        updateSongBtn = (Button) findViewById(R.id.update_song_button);
        syncSongBtn = (Button) findViewById(R.id.sync_song_button);
        updateAllBtn = (Button) findViewById(R.id.update_now_button);
        autoSyncSongChkbox = (CheckBox) findViewById(R.id.checkbox_auto_sync);
        autoUpdateSongChkbox = (CheckBox) findViewById(R.id.checkbox_auto_update);

        // set value and action for widget
        currentVersionTxt.setText(R.string.current_version + " " + PrefStore.getLatestVersion(getApplicationContext()));

        updateSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        syncSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        updateAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}