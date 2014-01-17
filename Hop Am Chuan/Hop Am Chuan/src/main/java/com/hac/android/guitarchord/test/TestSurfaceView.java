package com.hac.android.guitarchord.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hac.chorddroid.components.ChordSurfaceView;
import com.hac.chorddroid.helper.DrawHelper;
import com.hac.android.guitarchord.R;

public class TestSurfaceView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.testsurfaceview_fragment_main);


        ImageView imgView = (ImageView) findViewById(R.id.imageView1);
        imgView.setImageDrawable(DrawHelper.getBitmapDrawable(getResources(), 200, 200, "Am",0,0));

        final Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Toast toast = new Toast(getApplicationContext());
                LayoutInflater inflater = getLayoutInflater();

                View layout = inflater.inflate(R.layout.chordsurfaceview_toast,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

//                TextView text = (TextView) layout.findViewById(R.id.customToastTextView);
//                text.setText("This is a custom toast");
                final ChordSurfaceView chord = (ChordSurfaceView) layout.findViewById(R.id.chordViewA);
                chord.drawChord("Am");
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
        // as you specify a parent mActivity in AndroidManifest.xml.
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
