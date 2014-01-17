package com.hac.android.guitarchord.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hac.android.guitarchord.MainActivity;

/**
 * Custom Fragment use to optimize whole system
 */
public abstract class CustomFragment extends Fragment {

    public MainActivity activity;

    /** Must-have empty constructor */
    public CustomFragment() {

    }

    @Override
    public String toString() {
        return super.toString();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getSlidingMenu().showContent();
        activity.getSlidingMenu().setEnabled(true);
    }

    public abstract int getTitle();

}


