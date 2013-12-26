package com.hqt.hac.helper.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * this AsyncTask using for Configuration Change such as Rotation
 * will always keep Activity in fresh state. and prevent memory leak
 * use this class nicer than use WeakReference
 * Activity uses this Task must be detach() on Destroy() and attach() at onCreate()
 * *Notes*
 *         Void : work : often uses Void. parameters can put into constructor
 *         Integer : uses to notify process state
 * Created by ThaoHQSE60963 on 12/26/13.
 */
public abstract class RotationAsyncTask extends AsyncTask {

    public Activity mActivity;
    Context mContext;
    ProgressDialog dialog;

    public RotationAsyncTask(Activity activity) {
        this.mActivity = activity;
        mContext = activity.getBaseContext();
        dialog = new ProgressDialog(activity);
    }

    public void detach() {
        mActivity = null;
        mContext = null;
    }

    public void attach(Activity activity) {
        this.mActivity = activity;
        mContext = activity.getBaseContext();
    }
}
