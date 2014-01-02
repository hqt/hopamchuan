package com.hqt.hac.helper.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.hqt.hac.helper.adapter.IInfinityAdapter;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.R;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.LOGI;
import static com.hqt.hac.utils.LogUtils.makeLogTag;


/**
 * ListView that when scroll to bottom will load more data
 *
 * Usage : (Step by Step)
 * 1. Initialize ListView
 * 2. Set Mode for this ListView :
 *      RunningBackground Mode : should hard work do on different thread
 *      NumPerLoading          : decide number of items at each time load.
 *      setFirstLoadingItems   : number of item to load after initiate the ListView.
 *      FirstProcessingLoading : should use lazy loading when first load ListView
 * 3. Don't forget to set adapter after all.
 * 4. Class use this Infinitive ListView should implement 1 method :
 *      b. List load (offset, count) : load list of song at `offset`, total `count` : return a list of object
 *
 * Created by ThaoHQSE60963 on 12/27/13.
 */
public class InfinityListView extends ListView implements AbsListView.OnScrollListener {

    public static String TAG = makeLogTag(InfinityListView.class);

    private static final int DEFAULT_FIRST_LOADING_ITEMS = 4;
    private static final int DEFAULT_NUM_PER_LOAD = 1;

    //region State variable to control current state of ListView
    /** variable to control is in current loading state or not */
    AtomicBoolean isLoading = new AtomicBoolean(false);
    /** variable to control result of action */
    boolean isSucceed = false;
    /** boolean variable to control should list view will load more or has come to end */
    AtomicBoolean isComeToEnd = new AtomicBoolean(false);
    /** maximum items first display */
    int firstLoadingItems = DEFAULT_FIRST_LOADING_ITEMS;
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
    /** return data each time loading */
    Collection loadedCollection;
    //endregion

    //region Configuration Variable control all Mode of Inf ListView
    /** variable to control should loading data should be on different thread or not */
    boolean isRunningBackground = true;
    /** variable to control number of items one time will be loading. just use when isGreedy set to true */
    int numPerLoading = DEFAULT_NUM_PER_LOAD;
    /** should this ListView process first loading for adapter */
    boolean isFirstProcessLoading = false;
    //endregion

    //region Constructor ListView
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
        setOnScrollListener(this);
        // create handler on same thread
        mHandler = new LoadingHandler();
    }

    /**
     * Reset ListView for reusable for example in a fragment with dropdown box, each
     * dropdown item represent different kind of item in the list.
     * @param adapter
     */
    public void resetListView(BaseAdapter adapter) {
//        footer.setVisibility(View.VISIBLE);
        // Remove first to make sure that there is no duplicate.
        if (footer != null) removeFooterView(footer);
        addFooterView(footer);
        isComeToEnd.set(false);
        isLoading.set(false);
        setAdapter(adapter);
        mAdapter.notifyDataSetChanged();
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// INTERNAL METHOD /////////////////////////////////////

    /** just override again this method to get Adapter
     * The adapter must implement IInfinityAdapter for dynamic adding items
     **/
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (BaseAdapter) adapter;
        /** it will loading until full of ListView */
        /** should make animation here for nicer view */
        if (isFirstProcessLoading) {
            for (int i = 0; i < firstLoadingItems; i++) {
                LOGE(TAG, "Load item: " + i);
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
        LOGE(TAG, "FirstVisibleItem:" + firstVisibleItem + "  VisibleItemCount:"
                 + visibleItemCount + "  TotalItemCount:" + totalItemCount);
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
                cleanState();
            }
        } catch (NullPointerException e) {
            // cause by when loading but activity change state (onPause() onDestroy() Configuration Change.
            // lead to NullPointerException
        }
    }

    /** decide to use greedy mode (load multi data at once) or not */
    private void longRunningTask(int index) {
        try {
            loadedCollection = mLoader.load(index, numPerLoading);
            // If there less than numPerLoad items, that mean the list is end.
            if (loadedCollection == null || loadedCollection.size() < numPerLoading) {
                isSucceed = false;
            } else {
                isSucceed = true;
            }
        } catch (NullPointerException e) {
            // prevent thread is running. and we shut down it.
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    ////////////////// HANDLER IMPLEMENTATION ////////////////////////////////////

    /** Using this interface for loading data and append data to new Adapter */
    /** Notes that load should perform on different thread and append must be perform on UI Thread */
    public interface ILoaderContent {
        Collection load(int offset, int count);
    }

    /**
     * using Handler for loading data background
     * after that. send Message to Message Queue that notify finish work
     */
    private class LoadingHandler extends Handler {
        /** this method will be called by send Message */
        @Override
        public void handleMessage(Message msg) {
           cleanState();
        }
    }

    /** clean state after finish work */
    private void cleanState() {
        // update data
        append();
        // has come to end list
        if (!isSucceed) {
            LOGE(TAG, "Remove FootView because come to end list");
            removeFooterView(footer);
//            setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//            setSelection(mAdapter.getCount() - 1);

            isComeToEnd.set(true);
            isLoading.set(false);
        } else {
            // update data for user
            mAdapter.notifyDataSetChanged();
            // restore state
            isLoading.set(false);
        }
    }

    /** Add the item into adapter **/
    private void append() {
        for (Object obj : loadedCollection) {
            ((IInfinityAdapter) mAdapter).addItem(obj);
        }
    }

    /////////////////////////////////////////////////////////////////////
    ////////////////// CONFIGURATION METHOD /////////////////////////////

    private View generateFooterView() {
        LinearLayout layout = new LinearLayout(getContext());
        ProgressBar loading = new ProgressBar(getContext());
        layout.addView(loading);
        return layout;
    }

    public void setListViewProperty(ListViewProperty property) {
        this.footer = property.footer;
        if (property.footerResourceId >= 0) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            footer = inflater.inflate(R.layout.list_item_loading, null);
        }
        if (footer == null) footer = generateFooterView();
        footer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
                // make this event to prevent NullPointException
            }
        });
        addFooterView(footer);
        this.mLoader = property.mLoader;
        this.mAdapter = property.mAdapter;
        this.isRunningBackground = property.isRunningBackground;
        this.numPerLoading = property.numPerLoading;
        this.isFirstProcessLoading = property.isFirstProcessLoading;
        this.firstLoadingItems = property.firstLoadingItems;
        // start running this ListView
        if (mAdapter != null) setAdapter(mAdapter);
    }

    public static class ListViewProperty {
        View footer = null;
        ILoaderContent mLoader = null;
        BaseAdapter mAdapter = null;
        boolean isRunningBackground = true;
        int numPerLoading = DEFAULT_NUM_PER_LOAD;
        int firstLoadingItems = DEFAULT_FIRST_LOADING_ITEMS;
        boolean isFirstProcessLoading = true;
        int footerResourceId = -1;


        /**
         * When set to false, task will be called directly, rather than from different thread.
         *
         * This is useful if for example have code to populate the adapter that already runs in a background thread,
         * and simply don't need the built in background functionality.
         *
         * When using this you must remember to call onDataReady() once already appended data.
         *
         */
        public ListViewProperty RunningBackground(boolean isRunningBackground) {
            this.isRunningBackground = isRunningBackground;
            return this;
        }

        /** set Loader do action for this inf ListView (should implement ILoaderInterface) */
        public ListViewProperty Loader(ILoaderContent loader) {
            this.mLoader = loader;
            return this;
        }

        /** set for first process loading. this behavior helps decrease delay when loading */
        public ListViewProperty FirstProcessLoading(boolean state) {
            this.isFirstProcessLoading = state;
            return this;
        }

        /** set number of items per loading. just use when greedy mode is set to true */
        public ListViewProperty NumPerLoading(int num) {
            this.numPerLoading = num;
            return this;
        }

        /** Set Adapter */
        public ListViewProperty Adapter(BaseAdapter adapter) {
            this.mAdapter = adapter;
            return this;
        }

        public ListViewProperty FirstLoadingItems(int n) {
            this.firstLoadingItems = n;
            return this;
        }

        /** Change Loading View */
        public ListViewProperty LoadingView(View v) {
            this.footer = v;
            return this;
        }

        public ListViewProperty LoadingView(int resourceId) {
            // inflate footer
            this.footerResourceId = resourceId;
            return this;
        }
    }
}

