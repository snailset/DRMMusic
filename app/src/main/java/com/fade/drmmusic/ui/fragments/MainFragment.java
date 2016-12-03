package com.fade.drmmusic.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fade.drmmusic.R;
import com.fade.drmmusic.utils.FLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/11/25.
 */

public class MainFragment extends Fragment {

    private AppCompatActivity mContext;
    private List<ImageView> mTabs = new ArrayList<>();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.bar_music)
    ImageView mBarMusic;

    @BindView(R.id.bar_disco)
    ImageView mBarDisco;

    @BindView(R.id.main_viewpager)
    ViewPager mMainViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (AppCompatActivity) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        setToolbar();
        setViewPager();
        return view;
    }

    private void setToolbar() {
        mContext.setSupportActionBar(mToolbar);
        final ActionBar ab = mContext.getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        mTabs.add(mBarMusic);
        mTabs.add(mBarDisco);
        mBarMusic.setSelected(true);
        mBarMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainViewPager.setCurrentItem(0);
                FLog.i("select music");
            }
        });

        mBarDisco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainViewPager.setCurrentItem(1);
                FLog.i("select disco");
            }
        });
    }

    private void setViewPager() {

        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new TabLocalFragment());
        pagerAdapter.addFragment(new TabNetFragment());
        mMainViewPager.setAdapter(pagerAdapter);
        mMainViewPager.setCurrentItem(0);
        switchTab(0);
        mMainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_search:
                FLog.i("select menu_search");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Toolbar中间的tab
     * @param position
     */
    public void switchTab(int position) {
        if (mTabs ==  null) return;
        for (ImageView tab : mTabs) {
            tab.setSelected(false);
        }
        mTabs.get(position).setSelected(true);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
