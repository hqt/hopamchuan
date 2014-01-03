package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.hqt.hac.helper.adapter.ChordViewImageAdapter;
import com.hqt.hac.helper.adapter.ChordViewTextureAdapter;
import com.hqt.hac.helper.adapter.IChordView;
import com.hqt.hac.helper.widget.FastSearchListView;
import com.hqt.hac.utils.ResourceUtils;
import com.hqt.hac.utils.UIUtils;
import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

import java.util.ArrayList;
import java.util.List;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class ChordViewFragment extends Fragment implements AdapterView.OnItemSelectedListener, IHacFragment {

    public static String TAG = makeLogTag(ChordViewFragment.class);

    public int titleRes = R.string.title_activity_chord_view;

    /** Main Activity for reference */
    private MainActivity activity;

    /** ListView : contains all ChordSurfaceView for current type of query */
    private List<String[]> typeOfChords = new ArrayList<String[]>();
    private ListView mChordSurfaceListView;

    /** ListView : contains all chords in system (defined in string resource) */
    private String[] mChordStrList;
    private ListView mChordListView;


    /** Adapter for this fragment */
    private IChordView adapter;

    /** spinner of this fragment
     * use for user select how to view chords (simple or advanced or all)
     */
    private Spinner spinner;

    public ChordViewFragment() {

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
        View rootView = inflater.inflate(R.layout.fragment_chord_view, container, false);

        /** Spinner configure */
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> choices = ArrayAdapter.
                createFromResource(getActivity().getApplicationContext(),
                        R.array.type_of_chord_view, R.layout.custom_spinner_item);
        // Specify the layout to use when the list of choices appears
        choices.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(choices);    // Apply the mAdapter to the spinner
        spinner.setOnItemSelectedListener(this);   // because this fragment has implemented method

        // load all chord to memory
        String[] _chordStr;
        _chordStr = ResourceUtils.loadStringArray(R.array.simple_chord);
        typeOfChords.add(_chordStr);
        _chordStr = ResourceUtils.loadStringArray(R.array.advanced_chord);
        typeOfChords.add(_chordStr);
        _chordStr = ResourceUtils.loadStringArray(R.array.all_chord);
        typeOfChords.add(_chordStr);

        /**
         * Left Panel :
         * ListView Configure for view all SurfaceView of chords at main screen
         */
        mChordSurfaceListView = (FastSearchListView) rootView.findViewById(R.id.list_chord_graphic);
        mChordSurfaceListView.setDivider(null);

        // custom Adapter base on current Android System
        if (UIUtils.hasICS()) {
            adapter = new ChordViewTextureAdapter(getActivity().getApplicationContext(), typeOfChords.get(0));
        } else {
            adapter = new ChordViewImageAdapter(getActivity().getApplicationContext(), typeOfChords.get(0));
        }
        mChordSurfaceListView.setAdapter((BaseAdapter)adapter);
        mChordSurfaceListView.setFastScrollEnabled(true);

        mChordSurfaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LOGE("TRUNGDQ", "view: " + view);
            }
        });

        return rootView;
    }


    /** Spinner : when user choose different method to view chord
     * reload all chords and refresh ListView
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0 :
                adapter.setChordList(typeOfChords.get(0));
                ((BaseAdapter)adapter).notifyDataSetChanged();
                break;
            case 1:
                adapter.setChordList(typeOfChords.get(1));
                ((BaseAdapter)adapter).notifyDataSetChanged();
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public int getTitle() {
        return titleRes;
    }
}
