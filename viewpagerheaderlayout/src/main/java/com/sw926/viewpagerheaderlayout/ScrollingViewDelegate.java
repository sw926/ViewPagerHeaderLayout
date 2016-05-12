package com.sw926.viewpagerheaderlayout;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by sunwei on 5/12/16.
 */
public class ScrollingViewDelegate {

    public static boolean canScrollVertical(View target, @ScrollingViewChild.SCROLL_DIRECTION int direction) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (target instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) target;
                if (absListView.getChildCount() == 0) {
                    return false;
                }
                return (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(target, direction) || target.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(target, direction);
        }
    }
}
