package com.hac.android.helper.widget;

import android.view.View;
import android.widget.AdapterView;

/**
 * Use this Listener instead of normal listener to know first time Adapter has been used
 * Created by ThaoHQSE60963 on 1/16/14.
 */
public abstract class FirstBloodAdapterView implements AdapterView.OnItemSelectedListener {

    boolean isFirstInitalize = true;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isFirstInitalize) {
            onItemSelectedFirstTime(parent, view, position, id);
            isFirstInitalize = false;
        } else {
            onItemSelectedMultiTime(parent, view, position, id);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public abstract void onItemSelectedFirstTime(AdapterView<?> parent, View view, int position, long id);

    public abstract void onItemSelectedMultiTime(AdapterView<?> parent, View view, int position, long id);
}
