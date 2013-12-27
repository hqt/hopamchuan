package com.hqt.hac.view;

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

import com.hqt.hac.config.PrefStore;
import com.hqt.hac.helper.task.AsyncActivity;
import com.hqt.hac.model.json.HACAccount;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.DialogUtils;
import com.hqt.hac.utils.NetworkUtils;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Activity just for login purpose
 * Use this mActivity will gain more benefit rather than using DialogBox
 * More easily to control work flow and sync data between app and web
 * *Note*: No actionbar. so jus pure mActivity
 */
public class LoginActivity extends AsyncActivity {

    public static String TAG = makeLogTag(LoginActivity.class);

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
        /*if (!NetworkUtils.isNetworkConnected(getBaseContext())) {
            AlertDialog dialog = DialogUtils.showAlertDialog(this, "Network Problem", "Check Your Wifi or 3G Network Again");
            dialog.show();
            return;
        }*/

        LOGE(TAG, txtUsername.getText() + "\t" + txtPassword.getText());
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
        PrefStore.setLoginUsername(context, account.username);
        PrefStore.setLoginPassword(context, account.password);
        PrefStore.setEmail(context, account.email);
        PrefStore.setUserImage(context, account.image);

        return 0;
    }

    @Override
    public void onProgressUpdate(Integer... progress) {
        if (progress[0] == 1) {
            dialog.setTitle("Đăng nhập");
            dialog.setMessage("Đang kiểm tra...");
        }

        if (progress[0] == 2) {
            dialog.setTitle("Đăng nhập");
            dialog.setMessage("Đang lấy thông tin...");
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
            AlertDialog dialog = DialogUtils.showAlertDialog(this, "Lỗi!", "Kết nối bị lỗi, vui lòng thử lại sau!");
            dialog.show();
        } else if (result == -2L) {
            AlertDialog dialog = DialogUtils.showAlertDialog(this, "Lỗi!", "Tài khoản hoặc mật khẩu không đúng!");
            dialog.show();
        }

        // if connect successfully. close Login Activity
        // and loading again Main Activity
        // *NOTE* Cannot use General Method here
        if (result == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Thành công")
                    .setMessage("Đăng nhập thành công, nhấn OK để bắt đầu đồng bộ.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
