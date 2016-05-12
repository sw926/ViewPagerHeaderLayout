package com.sw926.headerviewpager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.sw926.viewpagerheaderlayout.ScrollingViewChild;
import com.sw926.viewpagerheaderlayout.ScrollingViewDelegate;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleScrollingViewViewFragment extends Fragment implements ScrollingViewChild {

    private static final String TAG = SimpleScrollingViewViewFragment.class.getSimpleName();

    private ScrollView mNestedScrollView;

    public SimpleScrollingViewViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_scroll_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNestedScrollView = (ScrollView) view.findViewById(R.id.nested_scroll_view);
    }

    @Override
    public boolean canChildScrollVertically(int direction) {
        return ScrollingViewDelegate.canScrollVertical(mNestedScrollView, direction);
    }

}
