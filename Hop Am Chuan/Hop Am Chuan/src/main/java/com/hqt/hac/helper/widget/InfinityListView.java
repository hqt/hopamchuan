package com.hqt.hac.helper.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.R;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;


/**
 * ListView that when scroll to bottom will load more data
 * Created by ThaoHQSE60963 on 12/27/13.
 */
public class InfinityListView extends ListView implements AbsListView.OnScrollListener {

    public static String TAG = makeLogTag(InfinityListView.class);

    /** view for loading row effect */
    View footer;
    /** variable to control is in current loading state or not */
    AtomicBoolean isLoading = new AtomicBoolean(false);
    /** variable to control should loading data should be on different thread or not */
    boolean isRunningBackground = true;
    /** variable to control should load multi data in one time or just only one */
    boolean isGreedy = false;
    /** variable to control number of items one time will be loading. just use when isGreedy set to true */
    int numPerLoading = 0;
    /** loader : use to define load action */
    ILoaderContent mLoader;
    /** Handler : use to run task on different thread */
    LoadingHandler mHandler;
    /** animation for rotation */
    RotateAnimation rotate;
    /** Adapter for this ListView. Keep this Adapter, because there's a time should set Adapter to null */
    BaseAdapter mAdapter;
    /** variable to control currently active index */
    int activeIndex;

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

    private void settingUpListView() {
        isLoading = new AtomicBoolean(false);
        isGreedy = false;
        isRunningBackground = true;
        setOnScrollListener(this);
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(R.layout.list_item_loading, null);
        mHandler = new LoadingHandler();
        rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(600);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
        addFooterView(footer);
    }

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

    /** set number of items per loading. just use when greedy mode is set to true */
    public void setNumPerLoading(int num) {
        this.numPerLoading = num;
    }

    /** Change Loading View */
    public void changeLoadingView(View v) { this.footer = v; }

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
    }

    ///////////////////////////////////////////////////////////////////////////
    ///////////////// INFINITY LOADING IMPLEMENTATION ////////////////////////

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        LOGE(TAG, "On Scroll State Changed");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (getAdapter() == null) return;
        LOGE(TAG, "On Scroll : Number of Items: " + getAdapter().getCount());
        if (getAdapter().getCount() == 0) return;

        // get the first Item that currently hide and need to show
        final int firstItemHide = firstVisibleItem + visibleItemCount;
        LOGE(TAG, "FirstVisibleItem:" + firstVisibleItem + "  VisibleItemCount:"
                + visibleItemCount + "  TotalItemCount:" + totalItemCount);
        if (firstItemHide >= totalItemCount && !isLoading()) {
            isLoading.set(true);
            activeIndex = firstVisibleItem;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    LOGE(TAG, "New Thread is running on " + NetworkUtils.getThreadSignature());
                    // run background
                   longRunningTask(10);
                    // notify data set to UI
                    mHandler.sendMessage(mHandler.obtainMessage());
                }
            });
            t.start();
        }
    }

    private void longRunningTask(int index) {
        if (isGreedy) {
            mLoader.load(index, index + numPerLoading);
        } else {
            mLoader.load(index);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    ////////////////// HANDLER IMPLEMENTATION ///////////////////////////////////

    /** Using this interface for loading data and append data to new Adapter */
    /** Notes that load should perform on different thread and append must be perform on UI Thread */
    public interface ILoaderContent {
        void load(int index);
        void load(int from, int to);
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
            LOGE(TAG, "Update Adapter on " + NetworkUtils.getThreadSignature());
            // update data
             mLoader.append();
            smallHack();
            // after change adapter. notify to adapter
            mAdapter.notifyDataSetChanged();
            setSelection(activeIndex-1);
            // restore state
            isLoading.set(false);
        }

        private void smallHack() {
            removeFooterView(footer);
            addFooterView(footer);
            setAdapter(mAdapter);
        }
    }
}
