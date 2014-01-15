package com.hqt.hac.helper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

import java.lang.reflect.Field;

public class TriggerSpinner extends Spinner {

    public TriggerSpinner(Context context) {
        super(context);
    }

    public TriggerSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); }

    public TriggerSpinner(Context context, AttributeSet attrs){
        super(context,attrs);
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