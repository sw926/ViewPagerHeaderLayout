package com.sw926.headerviewpager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabs;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mViewPager = (ViewPager) findViewById(R.id.vhl_viewpager);
        mTabs = (TabLayout) findViewById(R.id.tabs);

        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mTabs.setupWithViewPager(mViewPager);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       mRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    static class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SimpleFragment();
                case 1:
                    return new SimpleScrollingViewViewFragment();
                case 2:
                    return new SimpleListFragment();
                case 3:
                    return new SimpleRecyclerViewFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Normal";
                case 1:
                    return "ScrollView";
                case 2:
                    return "ListView";
                case 3:
                    return "RecyclerView";
            }
            return null;
        }
    }
}
