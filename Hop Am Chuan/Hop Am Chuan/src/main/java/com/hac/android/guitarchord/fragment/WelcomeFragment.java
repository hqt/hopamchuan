package com.hac.android.guitarchord.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hac.android.guitarchord.BunnyApplication;
import com.hac.android.guitarchord.R;

public class WelcomeFragment extends CustomFragment {

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
        SearchView mSearchView = (SearchView) rootView.findViewById(R.id.insert_chord_edit_text);

        // Get the SearchView and set the Search Configuration
        SearchManager searchManager = (SearchManager) BunnyApplication.getAppContext()
                .getSystemService(Context.SEARCH_SERVICE);
        // Assumes current mActivity is the searchable mActivity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        // Do not icon the widget. expand it.
        mSearchView.setIconifiedByDefault(false);
        // enable submit button
        mSearchView.setSubmitButtonEnabled(true);

        return rootView;
    }
}
