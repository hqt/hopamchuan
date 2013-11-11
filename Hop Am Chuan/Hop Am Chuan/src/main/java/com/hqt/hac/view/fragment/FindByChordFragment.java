package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.hqt.hac.helper.adapter.FindByChordAdapter;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindByChordFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    /** Main Activity for reference */
    MainActivity activity;

    /** ListView : contains all Chords can use to search of this fragment */
    ListView mListView;

    /** TextView for insert Chord */
    TextView insertChordTextView;

    /** Button for insert Chord to list */
    Button insertChordBtn;

    /** Button for Search Action */
    Button searchBtn;

    /** spinner of this fragment
     * use for user select base chords
     */
    Spinner spinner;

    /** Adapter for this fragment */
    FindByChordAdapter adapter;


    /**
     * List all chords base
     */
    String[] chordBase;

    /**
     * List all current chords need to search in listview
     */
    List<String> chords;

    public FindByChordFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_by_chord, container, false);

        /** using chord base from resource */
        chordBase = activity.getApplicationContext().getResources().getStringArray(R.array.chords_base_chord);
        /** get first result for default listview */
        chords = convertChordsToArray(chordBase[0]);

        // load all views
        insertChordTextView = (TextView) rootView.findViewById(R.id.insert_chord_edit_text);
        insertChordBtn = (Button) rootView.findViewById(R.id.add_chord_button);
        searchBtn = (Button) rootView.findViewById(R.id.search_btn);

        /** Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.chords_base_chord, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        /** ListView Configure */
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        adapter = new FindByChordAdapter(getActivity().getApplicationContext(), chords);
        mListView.setAdapter(adapter);

        /** add event for button */
        insertChordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence chord = insertChordTextView.getText();
                //chords.add(chord);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return rootView;
    }

    /** if user click
     * will refresh the list view
     * with base chords in resource
     * */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] chords = chordBase[position].split(", ");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

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

}
