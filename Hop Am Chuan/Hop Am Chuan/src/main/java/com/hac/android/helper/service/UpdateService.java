package com.hac.android.helper.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hac.android.config.PrefStore;
import com.hac.android.model.Song;
import com.hac.android.model.dal.SongDataAccessLayer;
import com.hac.android.model.json.DBVersion;
import com.hac.android.utils.APIUtils;
import com.hac.android.utils.LogUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Service for automatic update / sync data between client and server
 * Created by ThaoHQSE60963 on 1/6/14.
 */
public class UpdateService extends WakefulIntentService {

    private static String TAG = LogUtils.makeLogTag(UpdateService.class);

    public UpdateService() {
        super(TAG);
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        // update data
        // check version
        DBVersion version = APIUtils.getLatestDatabaseVersion(PrefStore.getLatestVersion());
        // no update need
        if (version == null || version.no == PrefStore.getLatestVersion()) {
            return;
        }

        // update songs
        List<Song> songs = APIUtils.getAllSongsFromVersion(PrefStore.getLatestVersion());
        if (songs == null) {
            return;
        }

        // TODO: this will cause lag with large data. Think later.
        // save to database
        boolean status = SongDataAccessLayer.insertFullSongListSync(getApplicationContext(), songs, null);
        if (status) {
            // set latest version to system after all step has successfully update
            PrefStore.setLastestVersion(version.no);
        }
    }
}

class UpdateServiceAlarm implements WakefulIntentService.AlarmListener {

    @Override
    public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context ctxt) {
        // Set the alarm to start at 1:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);

        // setRepeating() lets you specify a precise custom interval--in this case,
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, pi);
    }


    @Override
    public void sendWakefulWork(Context ctx) {
        WakefulIntentService.sendWakefulWork(ctx, UpdateService.class);
    }

    @Override
    public long getMaxAge() {
        return 0;
    }
}