package com.hqt.hac.view.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.dal.FavoriteDataAccessLayer;
import com.hqt.hac.model.dal.PlaylistDataAccessLayer;
import com.hqt.hac.utils.EncodingUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;
import com.hqt.hac.view.SettingActivity;

public class ProfilePopup {

    Activity activity;
    Dialog dialog;

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

        txtName.setText(PrefStore.getLoginUsername());
        txtMail.setText(PrefStore.getEmail());
        Bitmap imageAvatar = EncodingUtils.decodeByteToBitmap(PrefStore.getUserImage());

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
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private void logout() {
        Context context = activity.getApplicationContext();
        PrefStore.setLoginUsername( null);
        PrefStore.setLoginPassword(null);
        PrefStore.setEmail(null);
        PrefStore.setUserImage(null);

        // Remove all playlist, favorites
        PlaylistDataAccessLayer.removeAllPlaylists(context);
        FavoriteDataAccessLayer.removeAllFavorites(context);

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