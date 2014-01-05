package com.hqt.hac.utils;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

public class DialogUtils {
    /**
     * Create dropdown popup menu
     */
    public static PopupWindow createPopup(LayoutInflater inflater, int popupLayout) {
        View layout = inflater.inflate(popupLayout, null);

        if (layout != null) {
            layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            final PopupWindow popupWindow = new PopupWindow(layout , LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            // display the popup
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

            // Remember to set this or popup height will be messed up
            popupWindow.setHeight(layout.getMeasuredHeight());

            return popupWindow;
        }

        return null;
    }

    /** Create popup dialog  */
    public static Dialog createDialog(Activity theActivity, int titleStringResource, LayoutInflater inflater, int dialogLayout) {
        View layout = inflater.inflate(dialogLayout, null);
        Dialog dialog = new Dialog(theActivity);
        dialog.setContentView(layout);
        dialog.setTitle(titleStringResource);
        return dialog;
    }

    /**
     * Overload in case of user want to use view
     */
    public static Dialog createDialog(Activity theActivity, int titleStringResource, View layout) {
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

    public static void createNotification(Context context, Class activity, Bundle arguments, String title, String content, int notificationId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, activity);
        resultIntent.putExtra("notification", arguments);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(activity);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static void closeNotification(Context context, int notificationId) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.cancel(notificationId);
    }
}
