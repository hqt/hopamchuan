package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.hqt.hac.config.Config;
import com.hqt.hac.helper.adapter.FindByChordAdapter;
import com.hqt.hac.helper.widget.BackgroundContainer;
import com.hqt.hac.helper.widget.DeleteAnimListView;
import com.hqt.hac.model.Chord;
import com.hqt.hac.model.dal.ChordDataAccessLayer;
import com.hqt.hac.view.BunnyApplication;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;

public class SearchChordFragment extends Fragment implements
        AdapterView.OnItemSelectedListener,
        FindByChordAdapter.IFindByChordAdapter,
        IHacFragment {

    public int titleRes = R.string.title_activity_find_by_chord;

    /** Main Activity for reference */
    private MainActivity activity;

    /** ListView : contains all Chords can use to search of this fragment */
    private ListView mListView;

    /** TextView for insert Chord */
    private TextView insertChordTextView;

    /** Button for insert Chord to list */
    private Button insertChordBtn;

    /** Button for Search Action */
    private Button searchBtn;

    /** spinner of this fragment
     * use for user select base chords
     */
    private Spinner spinner;

    /** Adapter for this fragment */
    private FindByChordAdapter adapter;


    /**
     * List all chords base
     */
    private String[] chordBase;

    private BackgroundContainer mBackgroundContainer;

    private ComponentLoadHandler mHandler;
    private View rootView;

    public SearchChordFragment() {
    }


    @Override
    public int getTitle() {
        return titleRes;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_find_by_chord, container, false);

         /* Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.chords_base_chord, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        // Load component with a delay to reduce lag
        mHandler = new ComponentLoadHandler();
        Thread componentLoad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Config.LOADING_SMOOTHING_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });
        componentLoad.start();

        return rootView;
    }

    /** happen user click the spinner
     * will refresh the list view with new base chord list
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            adapter.setChords(convertChordsToArray(chordBase[position]));
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /** this action happen when user click [X] on list item */
    @Override
    public void removeChordFromList(int position) {
        adapter.removeChord(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * convert string resource to arraylist of chords
     * example : Am, Gm, C --> [Am,Gm,C]
     */
    private List<String> convertChordsToArray(String chords) {
        String[] chordArr = chords.split(", ");

        /** this method is wrong
         * it returns java.utils.Array.ArrayList instead of java.utils.ArrayList
         * which is not currently implement some helper method such as add / remove
         * (because java.utils.Array.ArrayList is immutable list)
         */
        // return Arrays.asList(chordArr);

        // using simple and straight forward implementation
        List<String> res = new ArrayList<String>();
        for (String chord : chordArr) res.add(chord.trim());
        return res;
    }


    private void setUpComponents() {
        mBackgroundContainer = (BackgroundContainer) rootView.findViewById(R.id.listViewBackground);

        /** using chord base from resource */
        chordBase = activity.getApplicationContext().getResources().getStringArray(R.array.chords_base_chord);
        /* get first result for default ListView*/
        // load all views
        insertChordTextView = (TextView) rootView.findViewById(R.id.insert_chord_edit_text);
        insertChordBtn = (Button) rootView.findViewById(R.id.add_chord_button);
        searchBtn = (Button) rootView.findViewById(R.id.search_btn);

        // ListView Configure
        mListView = (DeleteAnimListView) rootView.findViewById(R.id.list_view);
        adapter = new FindByChordAdapter(getActivity().getApplicationContext(), this, convertChordsToArray(chordBase[0]));
        // adapter.setTouchListener(((DeleteAnimListView)mListView).getTouchListener());
        ((DeleteAnimListView)mListView).setmBackgroundContainer(mBackgroundContainer);
        ((DeleteAnimListView)mListView).setAdapter(adapter);
        mListView.setAdapter(adapter);


        /* add event for button */
        insertChordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chord = "";
                if (insertChordTextView.getText() != null) {
                    chord = insertChordTextView.getText().toString();
                }
                /** Format chord name **/
                chord = chord.toLowerCase();

                // if user input nothing
                if (chord.length() == 0) return;

                // The first letter
                chord = Character.toString(chord.charAt(0)).toUpperCase() + chord.substring(1);

                Chord checkChord = ChordDataAccessLayer.getChordByName(
                        activity.getApplicationContext(), chord);

                if (checkChord != null) {
                    // remove focus of EditText
                    // by hiding soft keyboard
                    insertChordTextView.clearFocus();
                    insertChordTextView.requestFocus(EditText.FOCUS_DOWN);
                    InputMethodManager in = (InputMethodManager) activity.getApplicationContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(insertChordTextView.getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    if (chord.trim().length() > 0) {
                        boolean res = adapter.addChord(chord);
                        if (res) {
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.duplicate_chord, Toast.LENGTH_SHORT).show();
                        }
                    }

                    // clear data of EditText
                    insertChordTextView.setText("");
                    // go to end list
                    mListView.setSelection(adapter.getCount() - 1);
                } else {
                    Toast.makeText(activity.getApplicationContext(),
                            activity.getString(R.string.invalid_chord_name), Toast.LENGTH_LONG).show();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultFragment fragment = new SearchResultFragment();
                Bundle arguments = new Bundle();
                String chordSearch = adapter.getChords();
                if (chordSearch.length() == 0) {
                    Toast.makeText(BunnyApplication.getAppContext(), R.string.empty_list_search, Toast.LENGTH_SHORT).show();
                } else {
                    arguments.putString(Config.BUNDLE_KEYWORD, adapter.getChords());
                    arguments.putString(Config.BUNDLE_IS_CHORD_SEARCH, adapter.getChords());
                    fragment.setArguments(arguments);
                    activity.switchFragmentNormal(fragment);
                }
            }
        });
    }
    /////////////////
    //
    /////////////////
    private class ComponentLoadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            setUpComponents();
        }
    }

}
