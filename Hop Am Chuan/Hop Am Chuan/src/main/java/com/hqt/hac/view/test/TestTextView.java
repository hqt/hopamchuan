package com.hqt.hac.view.test;

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
import com.hqt.hac.utils.HacUtils;
import com.hqt.hac.utils.ParserUtils;
import com.hqt.hac.view.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestTextView extends ActionBarActivity {

    TextView testTextView;
    static int songCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testtextview_fragment_main);

        testTextView = (TextView) findViewById(R.id.testTextView);
        List<Song> songs = ParserUtils.getAllSongsFromResource(getApplicationContext());
        final String songContent = songs.get(++songCounter).getContent(getApplicationContext());
//        final String songContent = "[Am]Đó [Bm]là [Cm]một [Em]buổi [F]sáng [G]đầy [A]sương thu và gió [Cm]lạnh.\n" +
//                "[B#m79]Mẹ [A#m9]tôi [C]nắm [D]tay tôi dẫn đi [F]trên con đường [Dm]dài và hẹp.";
        if (testTextView != null) {
//            HacUtils.setSongFormatted(getApplicationContext(), testTextView, songContent);
            testTextView.setText(songContent);
            final ViewTreeObserver vto = testTextView.getViewTreeObserver();
            if (vto != null) {
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    volatile boolean isMySelf = false;
                    volatile boolean isSetFormatText = false;

                    @Override
                    public void onGlobalLayout() {
                        /**
                         * If the event is called inside this method, then do no action.
                         */
                        Log.i("TextViewDebug2", "Called: " + isMySelf);
                        Log.i("TextViewDebug2", "Text:\n" + testTextView.getText());
                        if (isMySelf) {
                            isMySelf = false;
                            return;
                        }

                        String orgStr = songContent;

                        if (isSetFormatText == false) {
                            // Set default text to get wrapped text
                            isSetFormatText = true;
                            testTextView.setText(songContent);
                            return;
                        } else {
                            isSetFormatText = false;
                            // Does the layout effected after .setText() method?
                            // If not, we have to do the 2 tasks

                            // Must do sperate task here:
                            // Task 1: re-set the paint text
                            // Task 2: set the formatted text

                            Log.i("TextViewDebug2", "Do next: ...");

                            /**
                             * Do smartwrap.
                             */
                            Layout layout = testTextView.getLayout();
                            int numLine = layout.getLineCount();

                            // This string to store the formatted lyric
                            String newStr = "";
                            Log.i("TextViewDebug", "orgStr: " + orgStr);

                            // Create formatted chord
                            for (int i = 0; i < numLine; ++i) {

                                // Get current lyric line
                                int startLine = layout.getLineStart(i);
                                int endLine = layout.getLineEnd(i);
                                StringBuilder curLine = new StringBuilder(orgStr.substring(startLine, endLine).trim());

                                // Create chord line
                                Pattern pattern = Pattern.compile("\\[.*?\\]");
                                Matcher matcher = pattern.matcher(curLine);
                                StringBuilder chordLine = new StringBuilder();

                                // End position of the last chord sign
                                int lastEnd = 0;
                                // Number of spaces to remove to synchronize with lyric text line
                                int stackDelete = 0;
                                //
                                int lastGroupLength = 0;
                                while (matcher.find()) {
                                    for (int j = lastEnd; j < matcher.start() - lastGroupLength; ++j) {
                                        chordLine.append(" ");
                                    }
                                    // Append the chord
//                                    chordLine.append(matcher.group().replace("[", "").replace("]", "") + "  ");
                                    chordLine.append(matcher.group());
                                    // Remove chord sign
                                    curLine.delete(matcher.start() - stackDelete, matcher.end() - stackDelete);
                                    // Increase stackDelete
                                    stackDelete += matcher.group().length();
                                    // Set the position
                                    lastEnd = matcher.end();
                                    // Set the last group length
                                    lastGroupLength = matcher.group().length();
                                }


                                // Add chord line
                                newStr += chordLine + "\n" + curLine.toString() + "\n";
                            }

                            // Re-set the smartwrapped text
                            isMySelf = true;
                            testTextView.setText(newStr);


                        } // End big if
                    }
                });

                ////////////////////
                ///////////
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        // Get text

                        // TODO add check if widget instanceof TextView
                        TextView tv = (TextView) view;
                        // TODO add check if tv.getText() instanceof Spanned
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);

                        String chordName = s.subSequence(start, end).toString().replace("[", "").replace("]", "");

                        Context context = getApplicationContext();
                        final Toast toast = new Toast(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View layout2 = inflater.inflate(R.layout.chordsurfaceview_toast, null);

                        //                  TextView text = (TextView) layout.findViewById(R.id.customToastTextView);
                        //                  text.setText("This is a custom toast");
                        final ChordSurfaceView chord = (ChordSurfaceView) layout2.findViewById(R.id.chordViewA);
                        chord.drawChord(chordName);

                        Button btnDismiss = (Button) layout2.findViewById(R.id.close);
                        btnDismiss.setText("X!");
                        btnDismiss.setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chord.nextPosition();
                            }
                        });

                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout2);
                        toast.show();
                    }
                }; // End clickable span


                SpannableString text = new SpannableString(testTextView.getText());

                Pattern pattern = Pattern.compile("\\[.*?\\]");
                Matcher matcher = pattern.matcher(text);
                // Check all occurrences
                while (matcher.find()) {
//                                System.out.print("Start index: " + matcher.start());
//                                System.out.print(" End index: " + matcher.end());
//                                System.out.println(" Found: " + matcher.group());
                    text.setSpan(clickableSpan, matcher.start(), matcher.end(), 0);
                }

            }
        } else {
            Log.i("Debug", "testTextView is null!");
        }

        // Increase font size
        findViewById(R.id.btnFUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, testTextView.getTextSize() + 5);
            }
        });

        // Decrease font size
        findViewById(R.id.btnFDo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, testTextView.getTextSize() - 5);
            }
        });

        // Increase transpose
        findViewById(R.id.btnTUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), testTextView, 1);
            }
        });

        // Decrease transpose
        findViewById(R.id.btnTDo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HacUtils.transposeTextView(getApplicationContext(), testTextView, -1);
            }
        });


    }

    public void testOutsideSetText(View v) {
        testTextView.setText("ABC Bitch!");
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
