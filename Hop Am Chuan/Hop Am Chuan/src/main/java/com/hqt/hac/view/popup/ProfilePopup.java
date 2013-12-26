package com.hqt.hac.view.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.config.PrefStore;
import com.hqt.hac.utils.EncodingUtils;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

public class ProfilePopup {

    Activity activity;
    Dialog dialog;
    EditText txtUsername;
    EditText txtPassword;
    Button loginButton;
    Button cancelButton;

    public ProfilePopup(Activity activity) {
        this.activity = activity;

        // Create custom dialog object
        dialog = new Dialog(activity);

        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_profile);

        // Set dialog title
        dialog.setTitle("Tài khoản");

        // Account info

        TextView txtName = (TextView) dialog.findViewById(R.id.name);
        TextView txtMail = (TextView) dialog.findViewById(R.id.mail);
        ImageView imgAvatar = (ImageView) dialog.findViewById(R.id.imageView);

        txtName.setText(PrefStore.getLoginUsername(activity.getApplicationContext()));
        txtMail.setText(PrefStore.getEmail(activity.getApplicationContext()));
        Bitmap imageAvatar = EncodingUtils.decodeByteToBitmap(PrefStore.getUserImage(activity.getApplicationContext()));

        if (imageAvatar != null) {
            imgAvatar.setImageBitmap(imageAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.default_avatar);
        }

        // Buttons
        TextView btnLogout = (TextView) dialog.findViewById(R.id.btnLogout);
        Button syncButton = (Button) dialog.findViewById(R.id.btnSync);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSyncDialog();
            }
        });


    }

    private void openSyncDialog() {

    }

    private void logout() {
        Context context = activity.getApplicationContext();
        PrefStore.setLoginUsername(context, null);
        PrefStore.setLoginPassword(context, null);
        PrefStore.setEmail(context, null);
        PrefStore.setUserImage(context, null);

        // Reload the mActivity
        dialog.dismiss();
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    // caller call this method for display LoginPopup
    public void show() {
        dialog.show();
    }
}