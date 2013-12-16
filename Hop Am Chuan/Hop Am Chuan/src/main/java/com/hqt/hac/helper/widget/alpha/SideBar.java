package com.hqt.hac.helper.widget.alpha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SectionIndexer;

public class SideBar extends View {
    private char[] l;
    private SectionIndexer sectionIndexter = null;
    //      private ListView list;
    private ExpandableListView mExListView;
    private static int m_nItemHeight = 27;
    int color=0xffff0000;
    Paint mPaint = new Paint();
    Bitmap mBitmap;
    Canvas mCanvas;
    BlurMaskFilter mBlur;
    public SideBar(Context context) {
        super(context);
        init();
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z' };
        setBackgroundColor(0x44FFFFFF);



        mBitmap = Bitmap.createBitmap(30, 1000, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mPaint.setColor(color);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        mPaint.setMaskFilter(mBlur);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setListView(ExpandableListView _list) {
        mExListView= _list;
        sectionIndexter = (SectionIndexer) _list.getExpandableListAdapter();



    }

    private void movePoint(float cy){
        mCanvas.drawColor(color, PorterDuff.Mode.CLEAR);
        mCanvas.drawCircle(15, cy, 10, mPaint);
        invalidate();
    }

    private void missPoint(){
        mCanvas.drawColor(color, PorterDuff.Mode.CLEAR);
        invalidate();
    }



    public boolean onTouchEvent(MotionEvent event) {
//              super.onTouchEvent(event);
//              movePoint(event.getY());

        int i = (int) event.getY();
        int idx = i / m_nItemHeight;
        if (idx >= l.length) {
            idx = l.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE) {
            movePoint(event.getY());

            if (sectionIndexter == null) {
                sectionIndexter = (SectionIndexer) mExListView.getAdapter();
            }
            int position = sectionIndexter.getPositionForSection(l[idx]);
            if (position == -1) {
                return true;
            }
//                      mExListView.setSelection(position);
            mExListView.setSelectedGroup(position);
            mExListView.expandGroup(position);
        }
        else if(event.getAction()== MotionEvent.ACTION_UP){
            missPoint();
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
//              super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        Paint paint = new Paint();
        paint.setColor(0xFFA6A9AA);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < l.length; i++) {
            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight
                    + (i * m_nItemHeight), paint);
        }
    }

    public void setItemHight(int itemHight){
        m_nItemHeight=itemHight;
    }
}
