package com.hac.android.helper.widget.alpha;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Dinh Quang Trung on 12/19/13.
 */
public class CustomScrollView extends ScrollView {

    public interface OnScrollListener {
        public void onScroll(int delta);
    }

    public OnScrollListener mOnScrollListener;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(oldt - t);
        }
    }
}
