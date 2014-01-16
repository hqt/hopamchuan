package com.hqt.hac.helper.widget;

import android.content.Context;
import android.util.Log;
import android.widget.Spinner;

import java.lang.reflect.Field;

/**
 * Spinner that fires all cases
 * Created by ThaoHQSE60963 on 1/14/14.
 */

public class TriggerSpinner extends Spinner {

    public TriggerSpinner(Context context) {
        super(context);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        ignoreOldSelectionByReflection();
        super.setSelection(position, animate);
    }

    private void ignoreOldSelectionByReflection() {
        try {
            Class<?> c = this.getClass().getSuperclass().getSuperclass().getSuperclass();
            Field reqField = c.getDeclaredField("mOldSelectedPosition");
            reqField.setAccessible(true);
            reqField.setInt(this, -1);
        } catch (Exception e) {
            Log.d("Exception Private", "ex", e);
        }
    }

    @Override
    public void setSelection(int position) {
        ignoreOldSelectionByReflection();
        super.setSelection(position);
    }



}