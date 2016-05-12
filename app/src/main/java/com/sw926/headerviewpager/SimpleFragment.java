package com.sw926.headerviewpager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sw926.viewpagerheaderlayout.ScrollingViewChild;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleFragment extends Fragment implements ScrollingViewChild {


    public SimpleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple, container, false);
    }

    @Override
    public boolean canChildScrollVertically(int direction) {
        return false;
    }
}
