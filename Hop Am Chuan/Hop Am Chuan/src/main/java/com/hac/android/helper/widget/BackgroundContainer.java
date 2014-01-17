package com.hac.android.helper.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.hac.android.guitarchord.R;


/**
 * A FrameLayout get drawable images as background
 * Use this class for some purpose as using in Deleting ListView (when remove a line will appear background)
 * this image often use 9-patch image, and should put into drawable folder rather than others
 */
public class BackgroundContainer extends FrameLayout {

    boolean mShowing = false;
    Drawable mShadowedBackground;
    int mOpenAreaTop, mOpenAreaBottom, mOpenAreaHeight;
    boolean mUpdateBounds = false;

    public BackgroundContainer(Context context) {
        super(context);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BackgroundContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mShadowedBackground =
                getContext().getResources().getDrawable(R.drawable.shadowed_background);
    }

    public void showBackground(int top, int bottom) {
        setWillNotDraw(false);
        mOpenAreaTop = top;
        mOpenAreaHeight = bottom;
        mShowing = true;
        mUpdateBounds = true;
    }

    public void hideBackground() {
        setWillNotDraw(true);
        mShowing = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mShowing) {
            if (mUpdateBounds) {
                mShadowedBackground.setBounds(0, 0, getWidth(), mOpenAreaHeight);
            }
            canvas.save();
            canvas.translate(0, mOpenAreaTop);
            mShadowedBackground.draw(canvas);
            canvas.restore();
        }
    }

}
