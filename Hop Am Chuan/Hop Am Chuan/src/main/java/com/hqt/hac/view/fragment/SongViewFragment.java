package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

import static com.hqt.hac.utils.LogUtils.makeLogTag;

public class SongViewFragment extends Fragment {
    public static String TAG = makeLogTag(ChordViewFragment.class);

    /** Main Activity for reference */
    MainActivity activity;

    public SongViewFragment() {

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

        return rootView;
    }
}
