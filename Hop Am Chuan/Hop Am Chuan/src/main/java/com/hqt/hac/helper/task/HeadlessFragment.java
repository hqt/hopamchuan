package com.hqt.hac.helper.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Use this fragment for running background task using AsyncTask
 * because Android retains Fragment (different from Activity). so itself will be survive across configuration changes
 * we will use this trick to fix configuration change when using AsyncTask
 * *Note* because Java does not support real Callback method. we create one for convenience
 *
 * Created by ThaoHQSE60963 on 12/26/13.
 */
public class HeadlessFragment extends Fragment {
    /** tag to recognize fragment signature */
    public static String TAG = makeLogTag(HeadlessFragment.class);

    /** AsyncTask for running background */
    public RotationAsyncTask task;

    /** callback method for running AsyncTask
     * in fact. it's our activity
     */
    public ITaskCallback mCallback;

    /** boolean variable to know is this work finish or not */
    private boolean isFinish = false;

    /** this method should be only called at first created */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //The heart and mind of headless fragment
        // It will keep the fragment alive during configuration change of activity
        setRetainInstance(true);

        // running long action
        task = new RotationAsyncTask();
        task.execute();
    }

    /** when configuration changes. attach again activity
     * this fragment just only is called when retained
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ITaskCallback) activity;
        ((AsyncActivity) activity).dialog = new ProgressDialog(activity);
    }

    /** when configuration changes. old activity is lost
     * assign Callback to null make Garbage Collector collects old activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Activity call this class to publish progress to UI
     */
    public void publishProgressToUI(int progress) {
        task.publishProgressToUI(progress);
    }

    /** if this work is finish. can set this work to null for another work */
    public boolean isFinish() {
        return  isFinish;
    }

    /** make this AsyncTask Private Inner to access Callback without get/set again */
    private class RotationAsyncTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallback != null) mCallback.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            isFinish = true;
            if (mCallback != null) mCallback.onPostExecute(status);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mCallback != null) mCallback.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mCallback != null) mCallback.onCancel();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (mCallback != null) return mCallback.doInBackground();
            else return -1;
        }

        /** because publishProgress is protected. use this method to public to outside */
        public void publishProgressToUI(Integer progress) {
            publishProgress(progress);

        }
    }
}