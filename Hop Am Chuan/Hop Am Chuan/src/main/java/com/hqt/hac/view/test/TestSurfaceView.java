package com.hqt.hac.view.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.hac_library.components.ChordSurfaceView;
import com.hqt.hac.view.R;

public class TestSurfaceView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.testsurfaceview_fragment_main);
        final Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                final Toast toast = new Toast(getApplicationContext());
//                LayoutInflater inflater = getLayoutInflater();
//
//                View layout = inflater.inflate(R.layout.chordsurfaceview_toast,
//                        (ViewGroup) findViewById(R.id.toast_layout_root));
//
////                TextView text = (TextView) layout.findViewById(R.id.customToastTextView);
////                text.setText("This is a custom toast");
//                ChordSurfaceView chord = (ChordSurfaceView) layout.findViewById(R.id.chordViewA);
//                chord.drawChord("Am");
//
//                Button btnDismiss = (Button)layout.findViewById(R.id.close);
//                btnDismiss.setText("X!");
//                btnDismiss.setOnClickListener(new Button.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        Log.i("Debug", "Cancel this toast.");
//                        toast.cancel();
//                    }
//                });
//
//                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//                toast.setDuration(Toast.LENGTH_LONG);
//                toast.setView(layout);
//                toast.show();

////////////////////////////////////////
                LayoutInflater inflater = getLayoutInflater();
                final View popupView = inflater.inflate(R.layout.chordsurfaceview_toast,
                        (ViewGroup) findViewById(R.id.toast_layout_root));


                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);


                Button btnDismiss = (Button) popupView.findViewById(R.id.close);
                btnDismiss.setText("Clickme!");
                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ChordSurfaceView chord = (ChordSurfaceView) popupView.findViewById(R.id.chordViewA);
                        chord.drawChord("Am");
                        Log.i("Debug", "New chord drawed!");
                    }
                });
                popupWindow.showAsDropDown(btn, 50, -30);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_surface_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.testsurfaceview_fragment_main, container, false);
            return rootView;
        }
    }

}
