package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hqt.hac.view.MainActivity;
import com.hqt.hac.view.R;

public class SettingFragment extends  Fragment {

    /** Main Activity for reference */
    MainActivity activity;

    public SettingFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // this.activity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);


        return rootView;
    }
}
