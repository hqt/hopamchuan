package com.hqt.hac.helper.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import com.hqt.hac.config.PrefStore;
import com.hqt.hac.model.HACAccount;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.MainActivity;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * String : Using Method by String to Download
 */
public class LoginAsyncTask extends AsyncTask<Void, Integer, Long>{

    public static String TAG = makeLogTag(LoginAsyncTask.class);

    String username;
    String password;
    Activity activity;
    Context context;
    ProgressDialog dialog;

    public LoginAsyncTask(Activity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;
        this.context = activity.getBaseContext();
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Long doInBackground(Void... params) {
        // parse account from network
       publishProgress(1);
        HACAccount account = APIUtils.validateAccount(username, password);
        if (account == null) {
            // Error from Server
            return -1L;
        } else if (account.username == null) {
            // Wrong password
            return -2L;
        }

        byte[] oldImage = account.image;

        // save this account to database and set image for MainActivity
        PrefStore.setLoginUsername(context, account.username);
        PrefStore.setLoginPassword(context, account.password);
        PrefStore.setEmail(context, account.email);
        PrefStore.setUserImage(context, account.image);

        /*
        byte[] newImage = PrefStore.getUserImage(context);
        if (newImage.length != oldImage.length) {
            LOGE(TAG, "FUCKING BYTE");
            LOGE(TAG, "OLD LENGTH: " + oldImage.length);
            LOGE(TAG, "NEW LENGTH: " + newImage.length);
        } else if (newImage.length == 0) {
            LOGE(TAG, "FUCKING LENGTH");
        } else {
            for (int i = 0; i < newImage.length; i++) {
                if (newImage[i] != oldImage[i]) {
                    LOGE(TAG, "FUCKING COMPARE");
                }
            }
            LOGE(TAG, "FUCKING END");
        }
        */

        return 0L;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (progress[0] == 1) {
            dialog.setTitle("Đăng nhập");
            dialog.setMessage("Đang kiểm tra...");
        }

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        if (progress[0] == 2) {
            dialog.setTitle("Đăng nhập");
            dialog.setMessage("Đang lấy thông tin...");
        }

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        dialog.show();
    }

    @Override
    protected void onPostExecute(Long result) {

        dialog.dismiss();

        if (result == -1L) {
            AlertDialog dialog = UIUtils.showAlertDialog(activity, "Lỗi!", "Kết nối bị lỗi, vui lòng thử lại sau!");
            dialog.show();
        } else if (result == -2L) {
            AlertDialog dialog = UIUtils.showAlertDialog(activity, "Lỗi!", "Tài khoản hoặc mật khẩu không đúng!");
            dialog.show();
        }


        // if connect successfully. close Login Activity
        // and loading again Main Activity
        // *NOTE* Cannot use General Method here
        if (result == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Thành công")
                    .setMessage("Đăng nhập thành công, nhấn OK để bắt đầu đồng bộ.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
