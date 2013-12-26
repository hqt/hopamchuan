package com.hqt.hac.helper.task;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by ThaoHQSE60963 on 12/26/13.
 */
public class AsyncActivity extends Activity {

    private RotationAsyncTask task=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task=(RotationAsyncTask)getLastNonConfigurationInstance();
        if (task != null) {
            task.attach(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public Object onRetainNonConfigurationInstance() {
        task.detach();
        return(task);
    }
}
