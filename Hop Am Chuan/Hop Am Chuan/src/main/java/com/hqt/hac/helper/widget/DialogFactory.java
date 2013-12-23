package com.hqt.hac.helper.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.hqt.hac.view.R;

/**
 * Created by Dinh Quang Trung on 12/23/13.
 */
public class DialogFactory {
    /**
     * Create dropdown popup menu
     * @param inflater
     * @param popupLayout
     * @return
     */
    public static PopupWindow createPopup(LayoutInflater inflater, int popupLayout) {
        View layout = inflater.inflate(popupLayout, null);

        final PopupWindow pw = new PopupWindow(layout , LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // display the popup in the center
        pw.setOutsideTouchable(true);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        pw.setFocusable(true);
        pw.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        return pw;
    }

    /**
     * Create popup dialog
     * @param theActivity
     * @param inflater
     * @param dialogLayout
     * @return
     */
    public static Dialog createDialog(Activity theActivity, int titleStringResource, LayoutInflater inflater, int dialogLayout) {
        View layout = inflater.inflate(dialogLayout, null);
        Dialog dialog = new Dialog(theActivity);
        dialog.setContentView(layout);
        dialog.setTitle(titleStringResource);
        return dialog;
    }
}
