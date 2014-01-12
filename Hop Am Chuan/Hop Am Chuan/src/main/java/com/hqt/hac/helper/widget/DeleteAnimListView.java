package com.hqt.hac.helper.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.hqt.hac.utils.UIUtils;

import java.util.HashMap;
import java.util.Map;

import static com.hqt.hac.utils.LogUtils.LOGE;
import static com.hqt.hac.utils.LogUtils.makeLogTag;

/**
 * control animation on ListView when delete a row
 * this class controls safely to comparable back to lower API (API >= 10 can work well)
 * Usage :
 *      a. Using ListView as normal
 *      b. Set touch listener for Adapter
 *      c. Adapter uses this effect MUST implement StableAdapter to gain stable Id (not depend on its location)
 *         so it will consistency each time delete animation happen.
 *
 * Created by ThaoHQSE60963 on 1/11/14.
 */
public class DeleteAnimListView extends ListView {

    private static String TAG = makeLogTag(DeleteAnimListView.class);

    /** Constant variables for animation */
    public static final int SWIPE_DURATION = 250;
    public static final int MOVE_DURATION = 150;

    /** Adapter for this ListView */
    ArrayAdapter mAdapter;

    /** Using FrameLayout with 9-patch technique */
    BackgroundContainer mBackgroundContainer;

    /**
     * Use HashMap with Identity for supporting remove action
     * declare outside for performance : prevent initialize to many times
     * map from itemId to y-location of item (by using View.getTop())
     * by use this data structure, we ensure consistency, before and after animation happen
     */
    final Map<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

    /** to know currentX of view animation. default is 0. means is no-move */
    float mCurrentX = 0;

    /** to know current alpha of view. to make it animate. default is 1 : mean show it 100% */
    float mCurrentAlpha = 1;

    /** variable to control is user swiping or not. Use to prevent user action */
    boolean mSwiping = false;

    boolean mItemPressed = false;

    /** variable to control is currently system is animated or not. if yes, stop all action to control consistency */
    boolean mAnimating = false;

    public DeleteAnimListView(Context context) {
        super(context);
    }

    public DeleteAnimListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DeleteAnimListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setAdapter(ArrayAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void setmBackgroundContainer(BackgroundContainer container) {
        this.mBackgroundContainer = container;
    }

    public OnTouchListener getTouchListener() { return mTouchListener; }

    float mDownX;

    private int mSwipeSlop = -1;

    /**
     * Assign this method to Adapter using this ListView
     *
     * OnTouch : include base action of user ;
     * DOWN    : Set all control variable to down state
     * UP      : See current location is enough to delete or not (and animate again to normal view or delete row view)
     * CANCEL  : Set all control variable to cancel state . and animate current view to base view using setSwipePosition()
     * MOVE    : Use old and new coordinate to decide which part of background will be showed
     */
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @SuppressLint("NewAPI")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    if (mAnimating) {
                        // Multi-item swipes not handled for easy configuration
                        return true;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    break;
                case android.view.MotionEvent.ACTION_CANCEL:
                    // avoid use following two line function
                    // because it doesn't support in older device
                    // v.setAlpha(1);
                    // v.setTranslationX(0);
                    mItemPressed = false;
                    setSwipePosition(v, 0);
                    break;
                /**
                 * when android system receive action move
                 * if current is moving. show background
                 * if current not move, mark as move, and start to show background animation as above
                 *  */
                case android.view.MotionEvent.ACTION_MOVE:
                {
                    if (mAnimating) {
                        return true;
                    }
                    float x = event.getX();
                    // if current API >= 13. can call getTranslation() function
                    if (UIUtils.hasHoneycomb()) {
                        x += v.getTranslationX();
                    }
                    // variable to mark how far user has move their finger
                    float deltaX = x - mDownX;
                    // just get abs value because we treat left swipe and right swipe as same behaviour
                    float deltaXAbs = Math.abs(deltaX);
                    if (!mSwiping) {
                        // signature to know that user have swipe enough, that we mark this as an delete action
                        if (deltaXAbs > mSwipeSlop) {
                            mSwiping = true;
                            // prevent silly action from user
                            requestDisallowInterceptTouchEvent(true);
                            // show background. equal to current view
                            mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                        }
                    }
                    if (mSwiping) {
                        /** avoid because not comparable with API <= 13. set custom function instead */
                        // v.setTranslationX((x - mDownX));
                        // v.setAlpha(1 - deltaXAbs / v.getWidth());
                        setSwipePosition(v, deltaX);
                    }
                }
                break;
                case android.view.MotionEvent.ACTION_UP:
                {
                    /** if animating. return true. mark as receive nothing from user. avoid silly action */
                    if (mAnimating) {
                        return true;
                    }
                    // User let go - figure out whether to animate the view out, or back into place
                    if (mSwiping) {
                        /** same step as above. view above for explanation */
                        // float x = event.getX() + v.getTranslationX();
                        float x = event.getX();
                        if (UIUtils.hasHoneycomb()) {
                            x += v.getTranslationX();
                        }
                        float deltaX = x - mDownX;
                        float deltaXAbs = Math.abs(deltaX);

                        float fractionCovered;
                        float endX;
                        final boolean remove;
                        // Greater than a quarter of the width - animate it out
                        if (deltaXAbs > v.getWidth() / 4) {
                            fractionCovered = deltaXAbs / v.getWidth();
                            endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                            remove = true;
                        }
                        // Not far enough - animate it back
                        else {
                            fractionCovered = 1 - (deltaXAbs / v.getWidth());
                            endX = 0;
                            remove = false;
                        }
                        /** Animate View base on above assumption */
                        // NOTE: This is a simplified version of swipe behavior
                        // A real version should use velocity (via the VelocityTracker class)
                        // to send the item off or back at an appropriate speed.
                        long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                        animateSwipe(v, endX, duration, remove);
                        setEnabled(false);
                    } else {
                        mItemPressed = false;
                    }
                }
                break;
                default:
                    return false;
            }
            return true;
        }
    };

    /** Helper method : We use helper method instead of native API for comparable old API */

    /**
     * Animates a swipe of the item either back into place or out of the ListView container
     * @param view  current view (is a row of ListView) will be animated
     * @param endX  endX location that animation will go to (alpha, location ...) as Flash :)
     * @param duration duration that animation should behave
     * @param remove variable to control is remove or not.
     *               if remove : alpha set to 0 : make it lighter until cannot see
     *               if not remove : restore state (when know that user doesn't want to remove yet)
     *               so, make it darker until normal
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @SuppressLint("NewAPI")
    private void animateSwipe(final View view, float endX, long duration, final boolean remove) {
        mAnimating = true;
        // block ListView to prevent silly action
        setEnabled(false);
        if (UIUtils.hasHoneycomb()) {
            view.animate().setDuration(duration)
                    .alpha(remove ? 0 : 1).translationX(endX)       // set 0 or 1 of alpha base on current action (remove or not)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Restore animated values
                            view.setAlpha(1);
                            view.setTranslationX(0);
                            if (remove) {
                                animateOtherViews(view);
                            } else {
                                mBackgroundContainer.hideBackground();
                                mSwiping = false;
                                mAnimating = false;
                                setEnabled(true);
                            }
                            mItemPressed = false;
                        }
                    });
        } else {
            // API >= 13. Feel easy to apply animation
            TranslateAnimation swipeAnim = new TranslateAnimation(mCurrentX, endX, 0, 0);
            AlphaAnimation alphaAnim = new AlphaAnimation(mCurrentAlpha, remove ? 0 : 1);
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(swipeAnim);
            set.addAnimation(alphaAnim);
            set.setDuration(duration);
            view.startAnimation(set);
            setAnimationEndAction(set, new Runnable() {
                @Override
                public void run() {
                    if (remove) {
                        animateOtherViews(view);
                    } else {
                        mBackgroundContainer.hideBackground();
                        mSwiping = false;
                        mAnimating = false;
                        setEnabled(true);
                    }
                    mItemPressed = false;
                }
            });
        }
    }

    /** Sets the horizontal position and translucency of the view beeing swipe
     *
     * @param view
     * @param deltaX
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewAPI")
    private void setSwipePosition(View view, float deltaX) {
        float fraction = Math.abs(deltaX) / view.getWidth();
        if (UIUtils.hasHoneycomb()) {
            view.setTranslationX(deltaX);
            view.setAlpha(1 - fraction);
        } else {
            // hard part. because older API doesn't have animator object
            // we should use translated animation system. And code is more complicate
            TranslateAnimation swipeAnim = new TranslateAnimation(deltaX, deltaX, 0, 0);
            mCurrentX = deltaX;
            mCurrentAlpha = (1 - fraction);
            AlphaAnimation alphaAnim = new AlphaAnimation(mCurrentAlpha, mCurrentAlpha);
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(swipeAnim);
            set.addAnimation(alphaAnim);
            set.setFillAfter(true);
            set.setFillEnabled(true);
            view.startAnimation(set);
        }
    }

    /**
     * Start to animate other row of ListView make delete action smoother
     * *Enhancement* : Use Map of Id to make this hard work more consistency !!!
     *  So, each row, we know its Id, and we know its top coordinate
     */
    private void animateOtherViews(View viewToRemove) {
        // get first position shows in ListView
        int firstVisiblePosition = getFirstVisiblePosition();

        // iterate through all currently row of ListView
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // because just relative. we use firstVisiblePosition to know currently position in list
            int position = firstVisiblePosition + i;
            long itemId = mAdapter.getItemId(position);
            if (child != viewToRemove) {
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        // Delete item from the Adapter. Simply use Remove method. So, just can use ArrayAdapter
        int position = getPositionForView(viewToRemove);
        LOGE(TAG, "Remove position: " + position);
        mAdapter.remove(mAdapter.getItem(position));
        // mAdapter.notifyDataSetChanged();

        /** After layout runs. capture position of all itemIds.
         *  compare to pre layout positions, and animate changes
         */
        final ViewTreeObserver observer = getViewTreeObserver();
        assert observer != null;
        /** Before redraw anything. watch again carefully */
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                LOGE(TAG, "Calling OnPreDrawListener");
                // remove currently listener, if any
                observer.removeOnPreDrawListener(this);
                // variable to control first animation
                boolean firstAnimation = true;
                int firstVisiblePosition = getFirstVisiblePosition();
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = mAdapter.getItemId(position);
                    LOGE(TAG, "First: " + firstVisiblePosition + "  position: " + position + "  Id: " + itemId);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop == null) {
                        // animate new views along with others.
                        // The catch is that they did not exist in the start state, so we must calculate their standing position
                        // based on whether they're coming in from the bottom (i > 0) or top
                        int childHeight = child.getHeight() + getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                    }
                    int delta = startTop - top;
                    if (delta != 0) {
                        Runnable endAction = firstAnimation ?
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mBackgroundContainer.hideBackground();
                                        mSwiping = false;
                                        mAnimating = false;
                                        setEnabled(true);
                                    }
                                } : null;
                        firstAnimation = false;
                        moveView(child, 0, 0, delta, 0, endAction);
                    }
                }
                // finish animation. clear all data in map
                mItemIdTopMap.clear();
                return true;
            }
        });
    }

    /**
     * Animate view between start and end X/Y locations, using either od (pre-3.0) or new Animation APIS */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void moveView(View view, float startX, float endX, float startY, float endY, Runnable endAction) {
        if (UIUtils.hasHoneycomb()) {
            // From ICS. Support Object Animator
            view.animate().setDuration(MOVE_DURATION);
            if (startX != endX) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, startX, endX);
                anim.setDuration(MOVE_DURATION);
                anim.start();
                setAnimatorEndAction(anim, endAction);
                endAction = null;
            }
            if (startY != endY) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY);
                anim.setDuration(MOVE_DURATION);
                anim.start();
                setAnimatorEndAction(anim, endAction);
            }
        } else {
            // because current API <= 11. we cannot use ObjectAnimator.
            // Must use TranslateAnimation. harder and not robust as ObjectAnimator
            TranslateAnimation translator = new TranslateAnimation(startX, endX, startY, endY);
            translator.setDuration(MOVE_DURATION);
            view.startAnimation(translator);
            if ((endAction != null) && (view.getAnimation() != null)) {
                final Runnable finalEndAction = endAction;
                view.getAnimation().setAnimationListener(new AnimationListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        finalEndAction.run();
                    }
                });
            }
        }
    }

    /** use Adapter to prevent implement too many method. Just a nice trick :) */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewAPI")
    private void setAnimatorEndAction(Animator animator, final Runnable endAction) {
        if (endAction != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    endAction.run();
                }
            });
        }
    }

    private void setAnimationEndAction(Animation animation, final Runnable endAction) {
        if (endAction != null) {
            animation.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    endAction.run();
                }
            });
        }
    }

    /** Avoid having to implement every method in AnimationListener in every implementation class */
    public static class AnimationListenerAdapter implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
