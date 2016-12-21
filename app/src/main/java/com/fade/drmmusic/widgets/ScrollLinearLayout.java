package com.fade.drmmusic.widgets;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fade.drmmusic.utils.FLog;

/**
 * Created by SnailSet on 2016/12/5.
 */

public class ScrollLinearLayout extends LinearLayout {

    private View mDragView;
    private int mHeight0;
    private LinearLayout mLinearLayout;
    private RecyclerView mRecyclerView;
    private ViewDragHelper mViewDragHelper;
    private int mLastY = 0;
    public ScrollLinearLayout(Context context) {
        this(context, null);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                final int topBound =getPaddingTop();
//                final int bottomBound = getHeight()- mDragView.getHeight();
                final int newTop =Math.max(top, topBound);
                mLastY = newTop;
                return newTop;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                if (mLastY < getHeight()/2) {
                    mViewDragHelper.settleCapturedViewAt(0, 0);
                } else {
                    mViewDragHelper.settleCapturedViewAt(0, getHeight());
                }
                mLastY = 0;
                invalidate();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                FLog.i("onViewPositionChanged exec");
                if (top >= getHeight()) {
                    if (mFinishScrollLintener != null) {
                        mFinishScrollLintener.onFinishScroll();
                    }
                }
            }

            @Override
            public int getViewVerticalDragRange(View child)
            {
                System.out.println(child instanceof LinearLayout);
                System.out.println(child instanceof TextView);
                System.out.println(child instanceof RecyclerView);
                FLog.i("getViewVerticalDragRange: " + (getMeasuredHeight()-child.getMeasuredHeight()));
                return 1;
            }
        });
    }

    private OnFinishScrollLintener mFinishScrollLintener;

    public void setOnFinishScroll(OnFinishScrollLintener finishScroll) {
        this.mFinishScrollLintener = finishScroll;
    }

    public interface OnFinishScrollLintener {
        void onFinishScroll();
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        if (getChildCount() != 2) {
//            throw  new RuntimeException(ScrollLinearLayout.class.getSimpleName() + "must has two child views");
//        }
//        View v0 = getChildAt(0);
//        if (v0 instanceof  LinearLayout) {
//            mLinearLayout = (LinearLayout) v0;
//        }
//        View view = getChildAt(1);
//        if (view instanceof RecyclerView) {
//            mRecyclerView = (RecyclerView) view;
//            FLog.i("发现了recycler view");
//        }
//        mLinearLayout.getViewTreeObserver().addOnPreDrawListener(
//                new ViewTreeObserver.OnPreDrawListener() {
//
//                    @Override
//                    public boolean onPreDraw() {
//                        mLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
//                        mHeight0 = mLinearLayout.getHeight(); // 获取高度
//                        return true;
//                    }
//                });
        if (getChildCount() > 0) {
            mDragView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                FLog.i("onTouchEvent down");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                FLog.i("onTouchEvent move");
//                int y = (int) event.getY();
//                FLog.i("y: " + y + " mLastY: " + mLastY);
//                FLog.i("==================..... " + (y-mLastY));
//                ((LinearLayout) getParent()).scrollTo(0, mLastY-y);
//                break;
//            case MotionEvent.ACTION_UP:
//                FLog.i("onTouchEvent up");
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                FLog.i("dispatchTouchEvent down");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                FLog.i("dispatchTouchEvent move");
//
////                setTranslationY(y-mLastY);
//                break;
//            case MotionEvent.ACTION_UP:
//                FLog.i("dispatchTouchEvent up");
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                FLog.i("onInterceptTouchEvent down");
//                mLastY = (int) ev.getY();
//                FLog.i("height0 " + mHeight0 + " lastY " + mLastY);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                FLog.i("onInterceptTouchEvent move");
//                if (mLastY < mHeight0) { // 点到了头部
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                FLog.i("onInterceptTouchEvent up");
//                break;
//        }
//        return true;
//    }

//    @Override
//    public void computeScroll() {
//        super.computeScroll();
//        if (mScrollber.computeScrollOffset()) {
//            scrollTo(0, mScrollber.getCurrY());
//            postInvalidate();
//        }
//    }
}
