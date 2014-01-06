package com.hqt.hac.helper.service;

import android.content.Intent;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.Song;
import com.hqt.hac.model.dal.SongDataAccessLayer;
import com.hqt.hac.model.json.DBVersion;
import com.hqt.hac.utils.APIUtils;

import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Service for automatic update / sync data between client and server
 * Created by ThaoHQSE60963 on 1/6/14.
 */
public class UpdateService extends WakefulIntentService {

    private static String TAG = makeLogTag(UpdateService.class);

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

        // save to database
        boolean status = SongDataAccessLayer.insertFullSongListSync(getApplicationContext(), songs);
        if (status) {
            // set latest version to system after all step has successfully update
            PrefStore.setLatestVersion(version.no);
        }
    }
}