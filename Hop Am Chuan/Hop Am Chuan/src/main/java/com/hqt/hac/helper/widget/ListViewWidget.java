package com.hqt.hac.helper.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hqt.hac.helper.adapter.IArrayAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * animation on ListView
 */
public class ListViewWidget {

    /** Constant variables for animation */
    public static final int SWIPE_DURATION = 250;
    public static final int MOVE_DURATION = 150;

    /**
     * Handle touch events to fade/move dragged items as they are swiped out
     * TODO : NEED TO EXTENDS TO BASE ADAPTER RATHER THAN ARRAY ADAPTER (THAOHQ)
     */
    public static View.OnTouchListener getTouchListener(final Context context, final ListView listView, final ArrayAdapter adapter, final BackgroundContainer mBackgroundContainer) {

        /**
         * Use HashMap with Identity for supporting remove action
         * declare outside for performance : prevent initialize to many times
         * map from itemId to y-location of item (by using View.getTop())
         */
        final Map<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();


        View.OnTouchListener mTouchListener;
        mTouchListener = new View.OnTouchListener() {

            float mDownX;
            private int mSwipeSlop = -1;

            boolean mSwiping = false;

            boolean mItemPressed = false;

            /** custom animation base on user animation */
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (mSwipeSlop < 0) {
                    mSwipeSlop = ViewConfiguration.get(context).getScaledTouchSlop();
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mItemPressed) {
                            // Multi-item swipes not handled
                            return false;
                        }
                        mItemPressed = true;
                        mDownX = event.getX();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setAlpha(1);
                        v.setTranslationX(0);
                        mItemPressed = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                    {
                        float x = event.getX() + v.getTranslationX();
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);
                        if (!mSwiping) {
                            if (deltaXAbs > mSwipeSlop) {
                                mSwiping = true;
                                listView.requestDisallowInterceptTouchEvent(true);
                                mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                            }
                        }
                        if (mSwiping) {
                            v.setTranslationX((x - mDownX));
                            v.setAlpha(1 - deltaXAbs / v.getWidth());
                        }
                    }
                    break;
                    case MotionEvent.ACTION_UP:
                    {
                        // User let go - figure out whether to animate the view out, or back into place
                        if (mSwiping) {
                            float x = event.getX() + v.getTranslationX();
                            float deltaX = x - mDownX;
                            float deltaXAbs = Math.abs(deltaX);
                            float fractionCovered;
                            float endX;
                            float endAlpha;
                            final boolean remove;
                            if (deltaXAbs > v.getWidth() / 4) {
                                // Greater than a quarter of the width - animate it out
                                fractionCovered = deltaXAbs / v.getWidth();
                                endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                                endAlpha = 0;
                                remove = true;
                            } else {
                                // Not far enough - animate it back
                                fractionCovered = 1 - (deltaXAbs / v.getWidth());
                                endX = 0;
                                endAlpha = 1;
                                remove = false;
                            }
                            // Animate position and alpha of swiped item
                            // NOTE: This is a simplified version of swipe behavior, for the
                            // purposes of this demo about animation. A real version should use
                            // velocity (via the VelocityTracker class) to send the item off or
                            // back at an appropriate speed.
                            long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                            listView.setEnabled(false);
                            v.animate().setDuration(duration).
                                    alpha(endAlpha).translationX(endX).
                                    withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Restore animated values
                                            v.setAlpha(1);
                                            v.setTranslationX(0);
                                            if (remove) {
                                                animateRemoval(v);
                                            } else {
                                                mBackgroundContainer.hideBackground();
                                                mSwiping = false;
                                                listView.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }
                    mItemPressed = false;
                    break;
                    default:
                        return false;
                }
                return true;
            }


            /**
             * This method animates all other views in the ListView container (not including ignoreView)
             * into their final positions. It is called after ignoreView has been removed from the
             * adapter, but before layout has been run. The approach here is to figure out where
             * everything is now, then allow layout to run, then figure out where everything is after
             * layout, and then to run animations between all of those start/end positions.
             *
             * Helper function bases on view object rather than position for general purpose
             */
            void animateRemoval(View viewToRemove) {

                int firstVisiblePosition = listView.getFirstVisiblePosition();
                // save all y-location of visible items on ListView (except remove item)
                for (int i = 0; i < listView.getChildCount(); ++i) {
                    View child = listView.getChildAt(i);
                    if (child != viewToRemove) {
                        int position = firstVisiblePosition + i;
                        long itemId = adapter.getItemId(position);
                        mItemIdTopMap.put(itemId, child.getTop());
                    }
                }

                // Delete the item from the adapter
                int position = listView.getPositionForView(viewToRemove);
                adapter.remove(adapter.getItem(position));

                /**
                 * TODO : DOCUMENT CLEARER HERE FOR REUSABLE (THAOHQ)
                 */
                final ViewTreeObserver observer = listView.getViewTreeObserver();
                if (observer == null) return;

                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        observer.removeOnPreDrawListener(this);
                        // first animation : mark animation for delete row before animate other rows
                        boolean firstAnimation = true;
                        int firstVisiblePosition = listView.getFirstVisiblePosition();

                        // listView.getCount() : return of all items in Adapter
                        // listView.getChildCount() : return number of subviews (often, is number of rows in screen and approximate ~= 10)
                        // important note : position of them will never relate to the actual item position in the adapter.
                        // so we must convert this position here to item position by using listView.getFirstVisiblePosition()
                        for (int i = 0; i < listView.getChildCount(); ++i) {
                            final View child = listView.getChildAt(i);
                            // real id of item on screen
                            int position = firstVisiblePosition + i;
                            long itemId = adapter.getItemId(position);
                            Integer startTop = mItemIdTopMap.get(itemId);
                            int top = child.getTop();
                            if (startTop != null) {
                                if (startTop != top) {
                                    int delta = startTop - top;
                                    child.setTranslationY(delta);
                                    child.animate().setDuration(MOVE_DURATION).translationY(0);
                                    // animated removed row
                                    if (firstAnimation) {
                                        child.animate().withEndAction(new Runnable() {
                                            public void run() {
                                                mBackgroundContainer.hideBackground();
                                                mSwiping = false;
                                                listView.setEnabled(true);
                                            }
                                        });
                                        firstAnimation = false;
                                    }
                                }
                            } else {
                                // this new view does not show anytime before
                                // so maybe at the bottom or the top
                                //
                                // Animate new views along with the others. The catch is that they did not
                                // exist in the start state, so we must calculate their starting position
                                // based on neighboring views.
                                int childHeight = child.getHeight() + listView.getDividerHeight();
                                startTop = top + (i > 0 ? childHeight : -childHeight);
                                int delta = startTop - top;
                                child.setTranslationY(delta);
                                child.animate().setDuration(MOVE_DURATION).translationY(0);
                                // animated removed row
                                if (firstAnimation) {
                                    child.animate().withEndAction(new Runnable() {
                                        public void run() {
                                            mBackgroundContainer.hideBackground();
                                            mSwiping = false;
                                            listView.setEnabled(true);
                                        }
                                    });
                                    firstAnimation = false;
                                }
                            }
                        }

                        // finish animation. clear all data in map
                        mItemIdTopMap.clear();
                        return true;
                    }
                });
            }
        };

        return mTouchListener;
    }

    /**
     * Helper method for retrieve item id of i-th item of visible rows in ListView
     */
    private static long getRealIdFromListView(ListView listView, int index) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        return firstVisiblePosition + index;
    }
}
