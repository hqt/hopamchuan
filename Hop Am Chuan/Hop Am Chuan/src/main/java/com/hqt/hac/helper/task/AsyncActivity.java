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
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        task.detach();

        return(task);
    }
}
