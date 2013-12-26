package com.hqt.hac.helper.task;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * A nice technical to solve problem with AsyncTask anc Configuration Change
 * Created by ThaoHQSE60963 on 12/26/13.
 * TODO Need to test carefully
 */
public abstract class AsyncActivity extends FragmentActivity implements ITaskCallback {

    HeadlessFragment fragment;
    // this dialog should put in this activity because it asscioate with activity
    public ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** extends class run this class to start to perform action */
    public void runningLongTask() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // find fragment is already created or not
        fragment = (HeadlessFragment) fragmentManager
                .findFragmentByTag(HeadlessFragment.TAG);

        // if current task is not started. start it !!!
        if (fragment == null || fragment.isFinish()) {
            fragment = new HeadlessFragment();
            fragmentManager.beginTransaction().add(fragment,
                    HeadlessFragment.TAG).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /** stimulate as using AsyncTask */
    public void publishProgress(int progress) {
        fragment.publishProgressToUI(progress);
    }

}

/** interface stimulate AsyncTask. Use this interface for Callback method */
interface ITaskCallback {
    void onPreExecute();
    Integer doInBackground();
    void onProgressUpdate(Integer... values);
    void onCancel();
    void onPostExecute(int status);
}
