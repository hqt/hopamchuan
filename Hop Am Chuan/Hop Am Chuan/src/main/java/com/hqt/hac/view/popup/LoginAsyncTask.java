package com.hqt.hac.view.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.HACAccount;
import com.hqt.hac.utils.APIUtils;

/**
 * String : Using Method by String to Download
 */
public class LoginAsyncTask extends AsyncTask<Void, Integer, Long>{

    String username;
    String password;
    Activity activity;
    Context context;
    ProgressDialog dialog;

    public LoginAsyncTask(Activity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.context = activity.getBaseContext();
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Long doInBackground(Void... params) {
        // parse account from network
       publishProgress(1);
        HACAccount account = APIUtils.validateAccount(username, password);
        if (account == null) {
            return -1L;
        }

        // save this account to database and set image for MainActivity
        PrefStore.setLoginUsername(context, account.username);
        PrefStore.setLoginPassword(context, account.password);
        PrefStore.setEmail(context, account.email);
        PrefStore.setUserImage(context, account.image);

        return 0L;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (progress[0] == 1) {
            dialog.setTitle("Progress");
            dialog.setMessage("Synchronize Account");
            return;
        }

        if (progress[0] == 2) {
            dialog.setTitle("Finish");
            dialog.setMessage("Update Image");
            return;
        }
    }

    @Override
    protected void onPostExecute(Long result) {
        if (result == -1) {
            dialog.setTitle("Notify");
            dialog.setMessage("Synchronize Account Fail");
            return;
        }

        if (result == 0) {
            dialog.setMessage("Synchronize Account Successfully");
        }
    }
}
