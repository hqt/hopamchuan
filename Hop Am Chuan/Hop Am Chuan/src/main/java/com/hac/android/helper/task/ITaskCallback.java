package com.hac.android.helper.task;

/**
 * Created by ThaoHQSE60963 on 1/6/14.
 */
/** interface stimulate AsyncTask. Use this interface for Callback method */
interface ITaskCallback {
    void onPreExecute();
    Integer doInBackground();
    void onProgressUpdate(Integer... values);
    void onCancel();
    void onPostExecute(int status);
}
