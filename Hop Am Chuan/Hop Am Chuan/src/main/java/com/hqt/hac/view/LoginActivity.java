package com.hqt.hac.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.helper.task.LoginAsyncTask;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Activity just for login purpose
 * Use this activity will gain more benefit rather than using DialogBox
 * More easily to control work flow and sync data between app and web
 * *Note*: No actionbar. so jus pure activity
 */
public class LoginActivity extends Activity {

    public static String TAG = makeLogTag(LoginActivity.class);

    EditText txtUsername;
    EditText txtPassword;
    Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.popup_login);

        // find Widget by Id
        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.btnLogin);

        // Register link
        TextView regLink = (TextView) findViewById(R.id.homepage_link);
        regLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.register_link));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        // add event
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncAccount();
            }
        });

//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void syncAccount() {
        if (!NetworkUtils.isNetworkConnected(getBaseContext())) {
            AlertDialog dialog = DialogUtils.showAlertDialog(this, "Network Problem", "Check Your Wifi or 3G Network Again");
            dialog.show();
            return;
        }
        LoginAsyncTask task = new LoginAsyncTask(this, txtUsername.getText().toString().trim(),
                txtPassword.getText().toString().trim());
        // task = new LoginAsyncTask(this, "test8", "123456");
        LOGE(TAG, txtUsername.getText() + "\t" + txtPassword.getText());
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
