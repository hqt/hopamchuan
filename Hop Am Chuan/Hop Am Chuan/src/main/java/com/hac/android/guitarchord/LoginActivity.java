package com.hac.android.guitarchord;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hac.android.config.PrefStore;
import com.hac.android.helper.task.AsyncActivity;
import com.hac.android.model.json.HACAccount;
import com.hac.android.utils.APIUtils;
import com.hac.android.utils.DialogUtils;
import com.hac.android.utils.LogUtils;
import com.hac.android.utils.UIUtils;

/**
 * Activity just for login purpose
 * Use this mActivity will gain more benefit rather than using DialogBox
 * More easily to control work flow and sync data between app and web
 * *Note*: No actionbar. so jus pure mActivity
 */
public class LoginActivity extends AsyncActivity {

    public static String TAG = LogUtils.makeLogTag(LoginActivity.class);

    EditText txtUsername;
    EditText txtPassword;
    Button loginButton;
    String username;
    String password;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Language setting
        UIUtils.setLanguage(getBaseContext());

        setContentView(R.layout.popup_login);
        context = getBaseContext();

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

    }

    private void syncAccount() {
        /*if (!NetworkUtils.isDeviceNetworkConnected(getBaseContext())) {
            AlertDialog dialog = DialogUtils.showAlertDialog(this, "Network Problem", "Check Your Wifi or 3G Network Again");
            dialog.show();
            return;
        }*/

        LogUtils.LOGE(TAG, txtUsername.getText() + "\t" + txtPassword.getText());
        username = txtUsername.getText().toString();
        password = txtPassword.getText().toString();

        // start to validate account
        runningLongTask();

        // task = new LoginAsyncTask(this, "test8", "123456");
        // task.execute();
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

    ///////////////////////////////////////////////////////////////
    ////////////// ASYNCTASK BACKGROUND CODE //////////////////////

    @Override
    public void onPreExecute() {

    }

    @Override
    public Integer doInBackground() {
        // parse account from network
        publishProgress(1);
        HACAccount account = APIUtils.validateAccount(username, password);
        if (account == null) {
            // Error from Server
            return -1;
        } else if (account.username == null) {
            // Wrong password
            return -2;
        }

        // byte[] oldImage = account.image;

        // save this account to database and set image for MainActivity
        PrefStore.setLoginUsername(account.username);
        PrefStore.setLoginPassword(account.password);
        PrefStore.setEmail(account.email);
        PrefStore.setUserImage(account.image);

        return 0;
    }

    @Override
    public void onProgressUpdate(Integer... progress) {
        if (progress[0] == 1) {
            dialog.setTitle(getString(R.string.login_account_title));
            dialog.setMessage(getString(R.string.checking_account));
        }

        if (progress[0] == 2) {
            dialog.setTitle(getString(R.string.login_account_title));
            dialog.setMessage(getString(R.string.get_account_info));
        }

        dialog.show();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onPostExecute(int result) {
        dialog.dismiss();

        if (result == -1L) {
            AlertDialog dialog = DialogUtils.showAlertDialog(this,
                    getString(R.string.notif_title_error),
                    getString(R.string.network_error));
            dialog.show();
        } else if (result == -2L) {
            AlertDialog dialog = DialogUtils.showAlertDialog(this,
                    getString(R.string.notif_title_error),
                    getString(R.string.wrong_password));
            dialog.show();
        }

        // if connect successfully. close Login Activity
        // and loading again Main Activity
        // *NOTE* Cannot use General Method here
        if (result == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.notif_title_success))
                    .setMessage(getString(R.string.login_success_click_to_sync))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
