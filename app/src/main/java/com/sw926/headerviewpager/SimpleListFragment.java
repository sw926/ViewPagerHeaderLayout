package com.sw926.headerviewpager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sw926.viewpagerheaderlayout.ScrollingViewChild;
import com.sw926.viewpagerheaderlayout.ScrollingViewDelegate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleListFragment extends Fragment implements ScrollingViewChild {


    public SimpleListFragment() {
        // Required empty public constructor
    }

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.listview);

        List<Map<String, String>> list = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("title", "item " + i);
            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, android.R.layout.simple_list_item_1, new String[]{"title"}, new int[]{android.R.id.text1});
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean canChildScrollVertically(int direction) {
        return ScrollingViewDelegate.canScrollVertical(mListView, direction);
    }
}
