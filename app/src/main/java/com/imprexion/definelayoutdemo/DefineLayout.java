package com.imprexion.definelayoutdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : gongh
 * @date : 2020/3/13 11:29
 * @desc : TODO
 */
public class DefineLayout extends ViewGroup {
    public static final String TAG = "DefineLayout";

    private final SparseArray<Integer> mWidthList = new SparseArray<>();
    private final List<Integer> mHeightList = new ArrayList<>();
    private final List<Integer> needResize = new ArrayList<>();
    private float mDownX;
    private float mDownY;
    private float mMoveX;
    private float mMoveY;
    private float mLastX;
    private float mLastY;

    boolean mCanScroll = false;
    private int mRealHeightSize = 0;
    private int mOldScrollY;
    private int mHeightSize;
    private Scroller mScroller;
    private View mChild;

    public DefineLayout(Context context) {
        this(context, null);
    }

    public DefineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "heightSize= " + mHeightSize);
        int childCount = getChildCount();
        mWidthList.clear();
        mHeightList.clear();
        needResize.clear();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            LayoutParams layoutParams = childAt.getLayoutParams();
//            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                needResize.add(i);
            }
            childAt.measure(getMeasuerSpac(widthMode, widthSize, layoutParams.width), getMeasuerSpac(heightMode, mHeightSize, layoutParams.height));
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            mWidthList.put(i, measuredWidth);
            mHeightList.add(measuredHeight);
        }

        // 處理 wrap-content
        LayoutParams layoutParams = getLayoutParams();
        if (layoutParams.width == LayoutParams.WRAP_CONTENT) {
            widthSize = 0;
            int size = mWidthList.size();
            for (int i = 0; i < size; i++) {
                int i1 = mWidthList.keyAt(i);
                Integer width = mWidthList.get(i1);
                widthSize = Math.max(widthSize, width + 120);
            }
        }

        if (layoutParams.height == LayoutParams.WRAP_CONTENT || layoutParams.height == LayoutParams.MATCH_PARENT) {
            mRealHeightSize = 0;
            for (int height : mHeightList) {
                mRealHeightSize += height;
                mRealHeightSize += 20;
            }
        } else {
            mRealHeightSize = mHeightSize;
        }


        if (mRealHeightSize > mHeightSize) {
            mCanScroll = true;
        }
        for (int i = 0; i < needResize.size(); i++) {
            int index = needResize.get(i);
            View childAt = getChildAt(index);
            childAt.measure(getMeasuerSpac(MeasureSpec.EXACTLY, widthSize, childAt.getLayoutParams().width), getMeasuerSpac(heightMode, mHeightSize, childAt.getLayoutParams().height));
            int measuredWidth = childAt.getMeasuredWidth();
            mWidthList.put(index, measuredWidth);
//            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
        }
        Log.d(TAG, "mRealHeightSize --> " + mRealHeightSize);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, widthMode), MeasureSpec.makeMeasureSpec(mRealHeightSize, heightMode));
    }

    private int getMeasuerSpac(int mode, int widthSize, int size) {
        int childMode = MeasureSpec.UNSPECIFIED;
        int childSize = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                if (size >= 0) {
                    childMode = MeasureSpec.EXACTLY;
                    childSize = size;
                } else if (size == LayoutParams.WRAP_CONTENT) {
                    childMode = MeasureSpec.AT_MOST;
                    childSize = widthSize;
                } else if (size == LayoutParams.MATCH_PARENT) {
                    childMode = MeasureSpec.EXACTLY;
                    childSize = widthSize;
                }
                break;
            case MeasureSpec.AT_MOST:
                if (size >= 0) {
                    childMode = MeasureSpec.EXACTLY;
                    childSize = size;
                } else if (size == LayoutParams.WRAP_CONTENT) {
                    childMode = MeasureSpec.AT_MOST;
                    childSize = widthSize;
                } else if (size == LayoutParams.MATCH_PARENT) {
                    childMode = MeasureSpec.AT_MOST;
                    childSize = widthSize;
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                if (size >= 0) {
                    childMode = MeasureSpec.EXACTLY;
                    childSize = size;
                } else if (size == LayoutParams.WRAP_CONTENT) {
                    childMode = MeasureSpec.UNSPECIFIED;
                    childSize = 0;
                } else if (size == LayoutParams.MATCH_PARENT) {
                    childMode = MeasureSpec.UNSPECIFIED;
                    childSize = 0;
                }
                break;
        }
        return MeasureSpec.makeMeasureSpec(childSize, childMode);
    }


    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        int nowTop = top;
        for (int j = 0; j < childCount; j++) {
            View childAt = getChildAt(j);
            childAt.layout(left, nowTop + 20, left + mWidthList.get(j), nowTop + mHeightList.get(j));
            nowTop += mHeightList.get(j);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "canScroll --> " + mCanScroll);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getX();
                mMoveY = event.getY();
                int deltaX = (int) (mMoveX - mDownX);
                int deltaY = (int) (mDownY - mMoveY);

                mOldScrollY = getScrollY();
                if (mOldScrollY < 0) {
                    mOldScrollY = 0;
                }

                if (mOldScrollY > mRealHeightSize - mHeightSize) {
                    mOldScrollY = mRealHeightSize - mHeightSize;
                }

                if (mCanScroll) {
//                    scrollTo(0, mOldScrollY + deltaY);
                    mScroller.startScroll(0, getScrollY(), 0, deltaY);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
//                mMoveX = event.getX();
//                mMoveY = event.getY();
//                int deltaX = (int) (mMoveX - mDownX);
//                int deltaY = (int) (mDownY - mMoveY);
//
//                mOldScrollY = getScrollY();
//                if (mOldScrollY < 0) {
//                    mOldScrollY = 0;
//                }
//
//                if (mOldScrollY > mRealHeightSize - mHeightSize) {
//                    mOldScrollY = mRealHeightSize - mHeightSize;
//                }
//
//                if (mCanScroll) {
////                    scrollTo(0, mOldScrollY + deltaY);
//                    mScroller.startScroll(0, getScrollY(), 0, deltaY);
//                    invalidate();
//                }
                break;
            default:
                break;
        }
        return true;
    }

    private View getChildByPosition(float downX, float downY, boolean b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int[] location = new int[2];
            childAt.getLocationInWindow(location);
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
//            int left = childAt.getLeft();
//            int right = childAt.getRight();
//            int top = childAt.getTop();
//            int bottom = childAt.getBottom();

            if (downX >= location[0] && downX <= location[0] + measuredWidth && downY >= location[1] && downY <= location[1] + measuredHeight) {
                if (b) {
                    return childAt;
                } else {
                    if (childAt == mChild) {
                        continue;
                    } else {
                        return childAt;
                    }
                }
            }
        }
        return null;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent EVENT = ACTION_DOWN");
                mLastX = ev.getX();
                mLastY = ev.getY();
                return super.onInterceptTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent EVENT = ACTION_MOVE");
                float currentX = ev.getX();
                float currentY = ev.getY();
                if (Math.abs(currentY - mLastY) > Math.abs(currentX - mLastX)) {
                    Log.d(TAG, "onInterceptTouchEvent return True");
                    return true;
                }
                mLastX = currentX;
                mLastY = currentY;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent EVENT = ACTION_UP");
                return super.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }
}
