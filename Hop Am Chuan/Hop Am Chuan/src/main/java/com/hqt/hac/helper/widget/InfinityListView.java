package com.hqt.hac.helper.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.hqt.hac.utils.NetworkUtils;
import com.hqt.hac.view.R;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;


/**
 * ListView that when scroll to bottom will load more data
 * Created by ThaoHQSE60963 on 12/27/13.
 */
public class InfinityListView extends ListView implements AbsListView.OnScrollListener {

    public static String TAG = makeLogTag(InfinityListView.class);

    View footer;
    boolean isLoading = false;
    ILoadingContent mLoader;
    LoadingHandler mHandler;

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
        isLoading = false;
        setOnScrollListener(this);
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = inflater.inflate(R.layout.list_item_loading, null);
        mHandler = new LoadingHandler();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        LOGE(TAG, "On Scroll State Changed");
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        LOGE(TAG, "On Scroll");
        if (getAdapter() == null) return;
        LOGE(TAG, "On Scroll : Number of Items: " + getAdapter().getCount());
        if (getAdapter().getCount() == 0) return;

        // get the first Item that currently hide and need to show
        final int firstItemHide = firstVisibleItem + visibleItemCount;
        LOGE(TAG, "Params: FirstVisibleItem:" + firstVisibleItem + "\tVisibleItemCount:"
                + visibleItemCount + "\tTotalItemCount:" + totalItemCount);
        if (firstItemHide >= totalItemCount && !isLoading) {
            this.addFooterView(footer);
            isLoading = true;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    LOGE(TAG, "Update Song on " + NetworkUtils.getThreadSignature());
                    // run background
                    mLoader.load(firstItemHide, firstItemHide + 10);
                    // notify data set to UI
                    mHandler.sendMessage(mHandler.obtainMessage());
                }
            });
            t.start();
        }
    }

    public interface ILoadingContent {
        void load(int index);
        void load(int from, int to);
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
            // update
            ((BaseAdapter) getAdapter()).notifyDataSetChanged();
            isLoading = true;
        }
    }
}
