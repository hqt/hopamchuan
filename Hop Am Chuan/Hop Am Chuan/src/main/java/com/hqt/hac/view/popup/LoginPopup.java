package com.hqt.hac.view.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.R;

public class LoginPopup {

    Activity activity;
    Dialog dialog;
    EditText txtUsername;
    EditText txtPassword;
    Button loginButton;
    Button cancelButton;

    public LoginPopup(Activity activity) {
        this.activity = activity;

        // Create custom dialog object
        dialog = new Dialog(activity);

        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_login);

        // Set dialog title
        dialog.setTitle("Đăng nhập");

        // find Widget by Id
        txtUsername = (EditText) dialog.findViewById(R.id.username);
        txtPassword = (EditText) dialog.findViewById(R.id.password);
        loginButton = (Button) dialog.findViewById(R.id.btnLogin);
        cancelButton = (Button) dialog.findViewById(R.id.btnCancel);

        // add event
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncAccount();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

    }

    public void syncAccount() {
        if (!NetworkUtils.isNetworkConnected(activity.getApplicationContext())) {
            dialog.dismiss();
            showAlertDialog();
            return;
        }
        LoginAsyncTask task = new LoginAsyncTask(activity, txtUsername.getText().toString().trim(),
                                                            txtPassword.getText().toString().trim());
        task.execute();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Lỗi kết nối")
                .setMessage("Không thể kết nối đến Internet. Vui lòng kiểm tra kết nối Wifi / 3G của bạn.")
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    // caller call this method for display LoginPopup
    public void show() {
        dialog.show();
    }
}