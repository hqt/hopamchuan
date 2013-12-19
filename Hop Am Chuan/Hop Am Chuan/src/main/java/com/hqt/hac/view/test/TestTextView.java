package com.hqt.hac.view.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hac_library.components.ChordSurfaceView;
import com.hac_library.helper.ChordHelper;
import com.hqt.hac.model.Song;
import com.hqt.hac.utils.APIUtils;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.ParserUtils;
import com.hqt.hac.view.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestTextView extends ActionBarActivity {

    TextView testTextView;
    static int songCounter = 0;
    Activity thisActivity = null;
    String songContent = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testtextview_fragment_main);
        thisActivity = this;
        testTextView = (TextView) findViewById(R.id.testTextView);

        // get content
        testTextView.setText(APIUtils.getAllSongsFromVersion(2));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_text_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_fontsize_up:
                testTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, testTextView.getTextSize() + 2);
                return true;
            case R.id.action_fontsize_down:
                testTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, testTextView.getTextSize() - 2);
                return true;
            case R.id.action_trans_up:
                HacUtils.transposeTextView(getApplicationContext(), testTextView, 1, thisActivity);
                return true;
            case R.id.action_trans_down:
                HacUtils.transposeTextView(getApplicationContext(), testTextView, -1, thisActivity);
                return true;
            case R.id.action_oneline:
                HacUtils.setSongFormatted(getApplicationContext(), testTextView, songContent, this);
                return true;
            case R.id.action_twoline:
                HacUtils.setSongFormattedTwoLines(getApplicationContext(), testTextView, songContent, this);
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
            View rootView = inflater.inflate(R.layout.testtextview_fragment_main, container, false);
            return rootView;
        }
    }

}
