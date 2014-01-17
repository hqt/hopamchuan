package com.hac.android.helper.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hac.android.config.PrefStore;
import com.hac.android.model.Song;
import com.hac.android.model.dal.SongDataAccessLayer;
import com.hac.android.model.json.DBVersion;
import com.hac.android.utils.APIUtils;
import com.hac.android.utils.DialogUtils;

import java.util.List;

public class UpdateSongAsyncTask extends AsyncTask<Void, Integer, Integer> {

    Activity activity;
    Context context;
    ProgressDialog dialog;

    public UpdateSongAsyncTask(Activity activity) {
        this.activity = activity;
        context = activity.getBaseContext();
        dialog = new ProgressDialog(activity);
        dialog.setTitle("Progress");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        // check version
        publishProgress(1);
        DBVersion version = APIUtils.getLatestDatabaseVersion(PrefStore.getLatestVersion());
        // no update need
        if (version == null || version.no == PrefStore.getLatestVersion()) {
            return 1;
        }

        // update songs
        publishProgress(2);
        List<Song> songs = APIUtils.getAllSongsFromVersion(PrefStore.getLatestVersion());
        if (songs == null) {
            return 2;
        }

        // save to database
        publishProgress(3);
        boolean status = SongDataAccessLayer.insertFullSongListSync(context, songs, null);
        if (status) return 3;
        else {
            // set latest version to system after all step has successfully update
            PrefStore.setLastestVersion(version.no);
            return 4;
        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        switch(progress[0]) {
            case 1:
                dialog.setMessage("Checking Version ...");
                break;
            case 2:
                dialog.setMessage("Downloading ...");
                break;
            case 3:
                dialog.setMessage("Updating ...");
                break;
            default:
                // do nothing
        }
        dialog.show();
    }

    @Override
    protected void onPostExecute(Integer result) {
        dialog.dismiss();

        switch (result) {
            // latest version
            case 1: {
                AlertDialog dialog = DialogUtils.showAlertDialog(activity, "Notify", "Already newest version");
                dialog.show();
                break;
            }

            // download or parse fail
            case 2: {
                AlertDialog dialog = DialogUtils.showAlertDialog(activity, "Error", "Network Error. Try again");
                dialog.show();
                break;
            }
            // update to database fail
            case 3: {
                AlertDialog dialog = DialogUtils.showAlertDialog(activity, "Error", "System Fail. Restart Application");
                dialog.show();
                break;
            }

            // successfully
            case 4: {
            }

            default:
                // do nothing
        }
    }
}
