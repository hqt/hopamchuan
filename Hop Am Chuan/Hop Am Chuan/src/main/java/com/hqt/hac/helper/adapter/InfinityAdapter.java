package com.hqt.hac.helper.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.R;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * Using this Adapter for load data when scroll till to end
 * Design class as Wrapper Adapter (see more Decorator Design Pattern)
 *
 * Usage :
 * TODO Add Usage Detail here
 * TODO NEED TO BE TEST CAREFULLY
 *
 * Created by ThaoHQSE60963 on 12/28/13.
 */
public class InfinityAdapter extends BaseAdapter {

    public static String TAG = makeLogTag(BaseAdapter.class);

    /** Real Adapter for Decorator */
    ListAdapter mAdapter;
    /** Context for inflating view. Avoid process on Activity Component or will throw Exception */
    Context mContext;
    /** view for loading row effect */
    View footer;
    /** pending view : use this view to set null when finish loading */
    View pendingView;
    /** variable to control is in current loading state or not. Using AtomicBoolean Type for ThreadSafe */
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

    public InfinityAdapter(Context context, ListAdapter adapter) {
        this.mContext = context;
        this.mAdapter = adapter;
        settingUpAdapter();
    }

    private void settingUpAdapter() {
        isLoading.set(false);
        isRunningBackground = true;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(R.layout.list_item_loading, null);
        mHandler = new LoadingHandler();
        rotate=new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(600);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);
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

    /** set for Greedy Mode or not */
    public void setGreedyMode(boolean mode) {
        this.isGreedy = mode;
    }

    /** set number of items per loading */
    public void setNumPerLoading(int num) {
        this.numPerLoading = num;
    }

    /** convenient method to know is loading or not */
    public boolean isLoading() {
        return isLoading.get();
    }

    /** Change Loading View */
    public void changeLoadingView(View v) {
        this.footer = v;
    }

    /** Set Loader for this Adapter */
    public void setLoader(ILoaderContent loader) {
        this.mLoader = loader;
    }

    private void setPendingView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pendingView = inflater.inflate(R.layout.list_item_loading, null);
    }

    //<editor-fold desc="Adapter Method">
    /////////////////////////////////////////////////////////////////////
    ///////////////////// ADAPTER METHOD ///////////////////////////////

    /**How many items currently in this adapter include loading items */
    @Override
    public int getCount() {
        if (isLoading()) {
            return mAdapter.getCount() + 1;
        } else {
            return mAdapter.getCount();
        }
    }

    @Override
    public Object getItem(int position) {
        // prevent for multi thread problem
        if (position >= mAdapter.getCount()) return null;
        else return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAdapter.getCount()) {
            return(IGNORE_ITEM_VIEW_TYPE);
        }
        return mAdapter.getItemViewType(position);
    }

    /** Are all items in Adapter enabled or not. If yes. all items are selectable and clickable */
    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    /** Returns true if the item at the specified position is something selectable. */
    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }
    //</editor-fold>

    ///////////////////////////////////////////////////////////////////////////
    ///////////////// INFINITY LOADING IMPLEMENTATION ////////////////////////

    /**
     *  Try catch when position is greater than current items in list
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LOGE(TAG, "Get View at " + position);
        if ((position + 1 == mAdapter.getCount()) || (position == mAdapter.getCount())) {
            LOGE(TAG, "Unsuccessfully " + position + " has came to End Screen");
            if (isRunningBackground && !isLoading()) {
                isLoading.set(true);
                // start on new thread
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOGE(TAG, "Update Song on " + NetworkUtils.getThreadSignature());
                        longRunningTask(position);
                        // notify data set to UI
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                });
                t.start();
            } else {
                // TODO thinking carefully here
                longRunningTask(position);
            }
            // return loading view
            setPendingView();
            return pendingView;
        } else {
            // nothing special happen. call base adapter
            return mAdapter.getView(position, convertView, parent);
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
            // update (already change last row view)
            mLoader.append();
            pendingView = null;
            notifyDataSetChanged();
            isLoading.set(false);
        }
    }
}