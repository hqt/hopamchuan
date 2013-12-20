package com.hqt.hac.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.popup.LoginAsyncTask;

/**
 * Activity just for login purpose
 * Use this activity will gain more benefit rather than using DialogBox
 * More easily to control work flow and sync data between app and web
 * *Note*: No actionbar. so jus pure activity
 */
public class LoginActivity extends Activity {

    EditText txtUsername;
    EditText txtPassword;
    Button loginButton;
    Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_login);

        // find Widget by Id
        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.btnLogin);
        cancelButton = (Button)findViewById(R.id.btnCancel);

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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void syncAccount() {
        if (!NetworkUtils.isNetworkConnected(getBaseContext())) {
            AlertDialog dialog = UIUtils.showAlertDialog(this, "Network Problem", "Check Your Wifi or 3G Network Again");
            dialog.show();
            return;
        }
        LoginAsyncTask task = new LoginAsyncTask(this, txtUsername.getText().toString().trim(),
                txtPassword.getText().toString().trim());

        // task = new LoginAsyncTask(this, "test8", "123456");

        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
