package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.hqt.hac.helper.adapter.ChordViewAdapter;
import com.hqt.hac.helper.adapter.FavoriteManagerAdapter;
import com.hqt.hac.utils.ResourceUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ChordViewFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    public static String TAG = makeLogTag(ChordViewFragment.class);

    /** Main Activity for reference */
    MainActivity activity;

    /** ListView : contains all ChordSurfaceView for current type of query */
    List<String[]> typeOfChords = new ArrayList<String[]>();
    ListView mChordSurfaceListView;

    /** ListView : contains all chords in system (defined in string resource) */
    String[] mChordStrList;
    ListView mChordListView;


    /** Adapter for this fragment */
    ChordViewAdapter adapter;

    /** spinner of this fragment
     * use for user select how to view chords (simple or advanced or all)
     */
    Spinner spinner;

    public ChordViewFragment() {

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
        View rootView = inflater.inflate(R.layout.fragment_chord_view, container, false);

        /** Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.type_of_chord_view, android.R.layout.simple_spinner_item);
      /*  ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.song_list_method, android.R.layout.simple_spinner_item);*/

        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        // load all chord to memory
        String[] _chordStr;
        _chordStr = ResourceUtils.loadStringArray(getActivity().getApplicationContext(), R.array.simple_chord);
        typeOfChords.add(_chordStr);
        _chordStr = ResourceUtils.loadStringArray(getActivity().getApplicationContext(), R.array.advanced_chord);
        typeOfChords.add(_chordStr);
        _chordStr = ResourceUtils.loadStringArray(getActivity().getApplicationContext(), R.array.all_chord);
        typeOfChords.add(_chordStr);

        /** ListView Configure for view all SurfaceView of chords at main screen */
        mChordSurfaceListView = (ListView) rootView.findViewById(R.id.list_chord_graphic);
        adapter = new ChordViewAdapter(getActivity().getApplicationContext(), typeOfChords.get(0));
        mChordSurfaceListView.setAdapter(adapter);

        return rootView;
    }


    /** Spinner : when user choose different method to view chord
     * reload all chords and refresh ListView
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0 :
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
