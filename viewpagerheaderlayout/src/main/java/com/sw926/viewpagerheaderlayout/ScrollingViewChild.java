package com.sw926.viewpagerheaderlayout;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sunwei on 5/11/16.
 */
public interface ScrollingViewChild {

    @Retention(RetentionPolicy.CLASS)
    @IntDef({SCROLL_UP, SCROLL_DOWN})
    @interface SCROLL_DIRECTION {
    }
    int SCROLL_UP = -1;
    int SCROLL_DOWN = 1;

    boolean canChildScrollVertically(@SCROLL_DIRECTION int direction);
}
