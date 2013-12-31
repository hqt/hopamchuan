package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hqt.hac.view.R;

public class WelcomeFragment extends  Fragment implements IHacFragment {

    public int titleRes = R.string.title_activity_welcome_fragment;

    public WelcomeFragment() {
    }



    @Override
    public int getTitle() {
        return titleRes;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        return rootView;
    }
}
