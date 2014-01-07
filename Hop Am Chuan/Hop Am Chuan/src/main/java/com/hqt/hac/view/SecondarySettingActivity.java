package com.hqt.hac.view;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.hqt.hac.config.Config;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Using for View app Content
 * If we use PreferenceActivity will nicer, but have some limit when doing background
 * (because has function update/sync data)
 * so, temporary use this class
 * Created by ThaoHQSE60963 on 1/6/14.
 */
public class SecondarySettingActivity extends Activity {

    private static String TAG = makeLogTag(SecondarySettingActivity.class);

    TextView currentAppTextView;
    TextView feedBackTextView;
    TextView rateAppTextView;
    TextView facebookTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_second);

        currentAppTextView = (TextView) findViewById(R.id.current_app_version);
        feedBackTextView = (TextView) findViewById(R.id.feed_back_txt);
        rateAppTextView = (TextView) findViewById(R.id.rate_app_txt);
        facebookTextView = (TextView) findViewById(R.id.facebook_group_txt);

        try {
            String versionName = getBaseContext().getPackageManager()
                    .getPackageInfo(getBaseContext().getPackageName(), 0).versionName;
            if (version != null)
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        feedBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "huynhquangthao@gmail.com", "trungdq88@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Hợp Âm Chuẩn");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        rateAppTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
                            + getApplicationContext().getPackageName())));
                }
            }
        });

        facebookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.FACEBOOK_GROUP));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        finish();
    }
}
