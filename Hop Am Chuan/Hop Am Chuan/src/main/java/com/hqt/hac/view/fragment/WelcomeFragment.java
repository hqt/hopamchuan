package com.hqt.hac.view.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.hqt.hac.view.BunnyApplication;
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
        /*TextView searchBox = (TextView) rootView.findViewById(R.id.insert_chord_edit_text);
        // Get the SearchView and set the Search Configuration
        SearchManager searchManager = (SearchManager) BunnyApplication.getAppContext()
                .getSystemService(Context.SEARCH_SERVICE);
        // Use MenuItemCompat for comparable backward with API 10
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchBox);
        // Assumes current mActivity is the searchable mActivity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Do not icon the widget. expand it.
        mSearchView.setIconifiedByDefault(false);
        // enable submit button
        mSearchView.setSubmitButtonEnabled(true);*/


        return rootView;
    }
}
