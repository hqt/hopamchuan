package com.hac.android.helper.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Custom ListView class
 * that support Indexing for Category
 */
public class FastSearchListView extends ListView {

    private boolean mIsFastScrollEnabled = false;
    private IndexScroller mScroller = null;
    private GestureDetector mGestureDetector = null;

    public FastSearchListView(Context context) {
        super(context);
    }

    public FastSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FastSearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFastScrollEnabled() {
        return mIsFastScrollEnabled;
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
        mScroller = new IndexScroller(getContext(), this);
        if (mIsFastScrollEnabled) {
            if (mScroller == null)
                mScroller = new IndexScroller(getContext(), this);
        } else {
            if (mScroller != null) {
                mScroller.hide();
                // mScroller = null;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        try {
            // Overlay index bar
            if (mScroller != null) {
                mScroller.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            // Intercept ListView's touch event
            if (mScroller != null && mScroller.onTouchEvent(ev))
                return true;

            if (mGestureDetector == null) {
                mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        // If fling happens, index bar shows
                        mScroller.show();
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }

                });
            }
            mGestureDetector.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(ev);
    }

    /** should return false. so that other event on item of ListView can itercept
     * if return true. so any specific event on item will take no effect
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mScroller != null)
            mScroller.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null)
            mScroller.onSizeChanged(w, h, oldw, oldh);
    }

}
