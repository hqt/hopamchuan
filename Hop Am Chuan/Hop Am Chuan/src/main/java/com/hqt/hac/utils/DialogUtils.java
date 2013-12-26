package com.hqt.hac.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class DialogUtils {
    /**
     * Create dropdown popup menu
     */
    public static PopupWindow createPopup(LayoutInflater inflater, int popupLayout) {
        View layout = inflater.inflate(popupLayout, null);

        final PopupWindow popupWindow = new PopupWindow(layout , LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // display the popup in the center
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        popupWindow.setFocusable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });

        return popupWindow;
    }

    /**
     * Create popup dialog
     */
    public static Dialog createDialog(Activity theActivity, int titleStringResource, LayoutInflater inflater, int dialogLayout) {
        View layout = inflater.inflate(dialogLayout, null);
        Dialog dialog = new Dialog(theActivity);
        dialog.setContentView(layout);
        dialog.setTitle(titleStringResource);
        return dialog;
    }

    public static AlertDialog showAlertDialog(Activity activity, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        return alert;
    }

    /**
     * decide location to show
     */
    public void location(PopupWindow popupWindow) {
        int height = popupWindow.getHeight();
        int width = popupWindow.getWidth();
    }

}
