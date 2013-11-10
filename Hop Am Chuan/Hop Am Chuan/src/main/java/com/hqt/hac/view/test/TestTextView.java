package com.hqt.hac.view.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.hac_library.helper.ChordHelper;
import com.hqt.hac.model.Song;
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.ParserUtils;
import com.hqt.hac.view.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestTextView extends ActionBarActivity {

    TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testtextview_fragment_main);

        testTextView = (TextView) findViewById(R.id.testTextView);
        List<Song> songs = ParserUtils.getAllSongsFromResource(getApplicationContext());
        final String songContent = songs.get(1).getContent(getApplicationContext());
        if (testTextView != null) {
            HacUtils.setSongFormatted(getApplicationContext(), testTextView, songContent);
//            ViewTreeObserver vto = testTextView.getViewTreeObserver();
//            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    Layout layout = testTextView.getLayout();
//                    int curLine = layout.getLineStart(1);
//                    int nextLine = layout.getLineStart(2);
//                    int numLine = layout.getLineCount();
//
//                    Log.i("TextViewDebug", "curLine: " + curLine + " | nextLine: " + nextLine + " | count: " + numLine);
//                }
//            });
        } else {
            Log.i("Debug", "testTextView is null!");
        }

        // Increase font size
        ((Button) findViewById(R.id.btnFUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, testTextView.getTextSize() + 1);
            }
        });

        // Decrease font size
        ((Button) findViewById(R.id.btnFDo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, testTextView.getTextSize() - 1);
            }
        });

        // Increase transpose
        ((Button) findViewById(R.id.btnTUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), testTextView, 1);
            }
        });

        // Decrease transpose
        ((Button) findViewById(R.id.btnTDo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), testTextView, -1);
            }
        });


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
            View rootView = inflater.inflate(R.layout.testtextview_fragment_main, container, false);
            return rootView;
        }
    }

}
