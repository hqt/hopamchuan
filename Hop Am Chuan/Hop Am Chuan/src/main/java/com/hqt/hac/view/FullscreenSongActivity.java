package com.hqt.hac.view;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.ListView;
import com.hqt.hac.helper.widget.SlidingMenuActionBarActivity;
import com.hqt.hac.view.util.SystemUiHider;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class FullscreenSongActivity extends SlidingMenuActionBarActivity {

    /** View for side bar */
     View sidebarView;
     /** Sliding Menu for Right View */
    SlidingMenu sidebar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // front view
        setContentView(R.layout.activity_song_fullscreen);

        // behind view
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        sidebarView = inflater.inflate(R.layout.song_right_bar, null);
        setBehindContentView(sidebarView);

        // Set up Sidebar
        setUpSideBar();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /** *NOTE* this sidebar lays from right */
    private void setUpSideBar() {
        sidebar = getSlidingMenu();

        // customize look for SlidingMenu
        sidebar.setMode(SlidingMenu.RIGHT);
        sidebar.setShadowWidthRes(R.dimen.shadow_width);
        sidebar.setShadowDrawable(R.drawable.shadowright);
        // recommend width for navigation drawer. use same for SlidingViewer
        sidebar.setBehindWidthRes(R.dimen.sidebar_width);
        sidebar.setFadeDegree(0.35f);

        // set custom action for SlidingMenu
        sidebar.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

    }
}
