package com.hqt.hac.helper.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hqt.hac.helper.adapter.InfinityAdapter;
import com.hqt.hac.helper.adapter.SongListAdapter;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.R;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.LOGI;
import static com.hqt.hac.utils.LogUtils.makeLogTag;


/**
 * ListView that when scroll to bottom will load more data
 * Created by ThaoHQSE60963 on 12/27/13.
 */
public class InfinityListView extends ListView implements AbsListView.OnScrollListener {

    public static String TAG = makeLogTag(InfinityListView.class);

    //region State variable to control current state of ListView
    /** variable to control is in current loading state or not */
    AtomicBoolean isLoading = new AtomicBoolean(false);
    /** variable to control result of action */
    boolean isSucceed = false;
    /** boolean variable to control should list view will load more or has come to end */
    AtomicBoolean isComeToEnd = new AtomicBoolean(false);
    /** maximum items first display */
    final int MAXIMUM_FIRST_LOADING = 4;
    //endregion

    //region Structure Object for this ListView
    /** view for loading row effect */
    View footer;
    /** loader : use to define load action */
    ILoaderContent mLoader;
    /** Handler : use to run task on different thread */
    LoadingHandler mHandler;
    /** Adapter for this ListView. Keep this Adapter, because there's a time should set Adapter to null */
    BaseAdapter mAdapter;
    //endregion

    //region Configuration Variable control all Mode of Inf ListView
    /** variable to control should loading data should be on different thread or not */
    boolean isRunningBackground = true;
    /** variable to control should load multi data in one time or just only one */
    boolean isGreedy = false;
    /** variable to control number of items one time will be loading. just use when isGreedy set to true */
    int numPerLoading = 0;
    /** should this ListView process first loading for adapter */
    boolean isFirstProcessLoading = false;
    //endregion


    public InfinityListView(Context context) {
        super(context);
        settingUpListView();
    }

    public InfinityListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        settingUpListView();
    }

    public InfinityListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        settingUpListView();
    }

    public void settingUpListView() {
        LOGE(TAG, "Setting Up ListView");
        isLoading = new AtomicBoolean(false);
        isGreedy = false;
        isRunningBackground = true;
        isFirstProcessLoading = false;
        setOnScrollListener(this);
        // inflate footer
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(R.layout.list_item_loading, null);
        // create handler on same thread
        mHandler = new LoadingHandler();
        addFooterView(footer);
    }


    public void resetListView(SongListAdapter adapter) {
        footer.setVisibility(VISIBLE);
        isComeToEnd.set(false);
        mAdapter.notifyDataSetChanged();
        setAdapter(adapter);
    }

    //region Option for this Inf ListView
    ////////////////////////////////////////////////////////////////////////
    //////////////////// GETTER / SETTER ///////////////////////////////////

    /**
     * When set to false, task will be called directly, rather than from different thread.
     *
     * This is useful if for example have code to populate the adapter that already runs in a background thread,
     * and simply don't need the built in background functionality.
     *
     * When using this you must remember to call onDataReady() once already appended data.
     *
     */
    public void setRunningBackground(boolean isRunningBackground) {
        this.isRunningBackground = isRunningBackground;
    }

    /** set Loader do action for this inf ListView (should implement ILoaderInterface) */
    public void setLoader(ILoaderContent loader) {
        this.mLoader = loader;
    }

    /** set for Greedy Mode or not */
    public void setGreedyMode(boolean mode) {
        this.isGreedy = mode;
    }

    /** set for first process loading. this behavior helps decrease delay when loading */
    public void setFirstProcessLoading(boolean state) { this.isFirstProcessLoading = state;}

    /** set number of items per loading. just use when greedy mode is set to true */
    public void setNumPerLoading(int num) {
        this.numPerLoading = num;
    }

    /** Change Loading View */
    public void changeLoadingView(View v) { this.footer = v; }
    //endregion

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// INTERNAL METHOD /////////////////////////////////////

    /** convenient method to know is loading or not */
    private boolean isLoading() {
        return isLoading.get();
    }

    /** just override again this method to get Adapter */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (BaseAdapter) adapter;
        /** it will loading until full of ListView */
        if (isFirstProcessLoading) {
            for (int i = 0; i < MAXIMUM_FIRST_LOADING; i++) {
                scheduleWork(i);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////// INFINITY LOADING IMPLEMENTATION ////////////////////////

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        LOGI(TAG, "On Scroll State Changed");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (getAdapter() == null) return;
        if (isComeToEnd.get()) return;
//        LOGI(TAG, "On Scroll : Number of Items: " + getAdapter().getCount());
        if (getAdapter().getCount() == 0) return;

        // get the first Item that currently hide and need to show
        final int firstItemHide = firstVisibleItem + visibleItemCount;
//        LOGE(TAG, "FirstVisibleItem:" + firstVisibleItem + "  VisibleItemCount:"
//                + visibleItemCount + "  TotalItemCount:" + totalItemCount);
        if (firstItemHide >= totalItemCount) {
            // scheduleWork(totalItemCount); << we don't count the loading item
            scheduleWork(totalItemCount - 1);
        }
    }

    /** decide to work on same thread or different thread */
    private void scheduleWork(final int index) {
        try {
            /**  waiting for ending session */
            if (isLoading.get()) return;
            isLoading.set(true);
            if (isRunningBackground) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOGE(TAG, "New Thread is running on " + NetworkUtils.getThreadSignature());
                        // run background
                        longRunningTask(index);
                        // notify data set to UI
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                });
                t.start();
            } else {
                longRunningTask(index);
            }
        } catch (NullPointerException e) {
            // cause by when loading but change state. lead to NullPointerException
            // clear this current ListView state to avoid memory leak
            if (mAdapter != null) mAdapter = null;
            if (mLoader != null) mLoader = null;
            if (mHandler != null) mHandler = null;
        }
    }

    /** decide to use greedy mode (load multi data at once) or not */
    private void longRunningTask(int index) {
        if (isGreedy) {
            isSucceed = mLoader.load(index, index + numPerLoading);
        } else {
            isSucceed = mLoader.load(index);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    ////////////////// HANDLER IMPLEMENTATION ////////////////////////////////////

    /** Using this interface for loading data and append data to new Adapter */
    /** Notes that load should perform on different thread and append must be perform on UI Thread */
    public interface ILoaderContent {
        boolean  load(int index);
        boolean  load(int from, int to);
        void append();
    }

    /**
     * using Handler for loading data background
     * after that. send Message to Message Queue that notify finish work
     */
    private class LoadingHandler extends Handler {
        /** this method will be called by send Message */
        @Override
        public void handleMessage(Message msg) {
            // update data
            mLoader.append();
            // has come to end list
            if (!isSucceed) {
                LOGE(TAG, "Remove FootView because come to end list");
                footer.setVisibility(INVISIBLE);
                setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                setSelection(mAdapter.getCount() - 1);
                isComeToEnd.set(true);
                // restore state
                isLoading.set(false);
            } else {
                // update data for user
                mAdapter.notifyDataSetChanged();
                // restore state
                isLoading.set(false);
            }
        }
    }
}