package com.sw926.viewpagerheaderlayout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by sunwei on 5/7/16.
 */
public class ViewPagerHeaderLayout extends ViewGroup implements NestedScrollingChild {

    private static final String TAG = ViewPagerHeaderLayout.class.getSimpleName();
    private static final int INVALID_POINTER = -1;
    private static final int SCROLL_DURATION = 200;

    private View mHeaderView;
    private View mContentView;
    private ViewPager mViewPager;

    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialDownY = -1;
    private float mLastMotionY;

    private int mTouchSlop;

    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private ScrollerCompat mScroller;

    public ViewPagerHeaderLayout(Context context) {
        this(context, null);
    }

    public ViewPagerHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller = ScrollerCompat.create(getContext());
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ensureTarget();


    }

    private void ensureTarget() {
        if (mHeaderView != null && mContentView != null) {
            return;
        }

        mHeaderView = findViewById(R.id.vhl_header);
        mContentView = findViewById(R.id.vhl_content);
        mViewPager = (ViewPager) findViewById(R.id.vhl_viewpager);

        if (mHeaderView == null || mContentView == null) {
            throw new IllegalStateException("can't find R.id.vhl_header/R.id.vhl_content");
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureTarget();

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int top = getPaddingTop();
        int left = getPaddingLeft();
        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();

        final View headerView = mHeaderView;
        int headerHeight = headerView.getMeasuredHeight();


        headerView.layout(left, top, left + childWidth, top + headerHeight);

        final View contentView = mContentView;

        int contentTop = top + headerView.getMeasuredHeight();
        contentView.layout(left, contentTop, left + childWidth, contentTop + childHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        ensureTarget();

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width = measureWidth - getPaddingLeft() - getPaddingRight();
        int height = measureHeight - getPaddingTop() - getPaddingBottom();

        int headerWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
        int headerHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);

        mHeaderView.measure(headerWidthMeasureSpec, headerHeightMeasureSpec);

        int contentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int contentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        mContentView.measure(contentWidthMeasureSpec, contentHeightMeasureSpec);


        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int headerHeight = mHeaderView.getMeasuredHeight();

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(viewHeight + headerHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                recycleVelocityTracker();

                mIsBeingDragged = false;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                mLastMotionY = initialDownY;

                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (mInitialDownY > 0 && Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
                    if (getScrollY() < getHeaderHeight()) {
                        mIsBeingDragged = true;
                    } else {
                        if (yDiff < 0) {
                            // up
                            if (!canChildScroll(ScrollingViewChild.SCROLL_DOWN)) {
                                mIsBeingDragged = true;
                            }
                        } else if (yDiff > 0) {
                            // down
                            if (!canChildScroll(ScrollingViewChild.SCROLL_UP)) {
                                mIsBeingDragged = true;
                            }
                        }
                    }
                    if (mIsBeingDragged) {
                        initOrResetVelocityTracker();
                        mVelocityTracker.addMovement(ev);
                    }
                }
                mLastMotionY = y;
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                mInitialDownY = -1;
                recycleVelocityTracker();
                break;
            }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                float deltaY = mLastMotionY - y;

                mLastMotionY = y;

                float destY = getScrollY() + deltaY;
                if (destY < 0) {
                    scrollTo(0, 0);
                } else if (destY > getHeaderHeight()) {
                    scrollTo(0, getHeaderHeight());
                } else {
                    scrollBy(0, (int) deltaY);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                mInitialDownY = -1;

                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(velocityTracker,
                        mActivePointerId);
                if (Math.abs(initialVelocity) > mMinimumVelocity) {
                    if (initialVelocity > 0) {
                        mScroller.startScroll(0, getScrollY(), 0, 0 - getScrollY(), SCROLL_DURATION);
                    } else {
                        mScroller.startScroll(0, getScrollY(), 0, getHeaderHeight() - getScrollY(), SCROLL_DURATION);
                    }
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                return false;
            }
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(ev);
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            scrollTo(x, y);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            mLastMotionY = (int) MotionEventCompat.getY(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private boolean canChildScroll(@ScrollingViewChild.SCROLL_DIRECTION int direction) {
        if (mViewPager != null) {
            Object object = mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
            if (object != null && object instanceof ScrollingViewChild) {
                return ((ScrollingViewChild) object).canChildScrollVertically(direction);
            }
        }
        return ScrollingViewDelegate.canScrollVertical(mContentView, direction);
    }

    private int getHeaderHeight() {
        return mHeaderView.getMeasuredHeight();
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (getScrollY() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return true;
    }
}
