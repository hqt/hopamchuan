package com.hac.android.guitarchord.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hac.android.config.PrefStore;
import com.hac.android.guitarchord.SettingActivity;
import com.hac.android.utils.EncodingUtils;
import com.hac.android.utils.HacUtils;
import com.hac.android.guitarchord.R;

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

        String username = PrefStore.getLoginUsername();
        String email = PrefStore.getEmail();

        if (username.isEmpty()) username = activity.getString(R.string.login_account);
        if (email.isEmpty()) email = activity.getString(R.string.login_account_description);

        txtName.setText(username);
        txtMail.setText(email);
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
        HacUtils.logout(activity, new HacUtils.AfterLogoutDelegate() {
            @Override
            public void onAfterLogout() {
                // Reload the mActivity
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);
            }
        });
    }

    // caller call this method for display LoginPopup
    public void show() {
        dialog.show();
    }
}