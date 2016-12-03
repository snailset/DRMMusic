package com.fade.drmmusic.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.fade.drmmusic.R;
import com.fade.drmmusic.utils.CommonUtils;
import com.fade.drmmusic.utils.FLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fade.drmmusic.R.id.toolbar;

/**
 * Created by SnailSet on 2016/12/1.
 */

public class LocalMusicFragment extends Fragment {

    @BindView(toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tabs)
    TabLayout mTab;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        ButterKnife.bind(this, view);
        setToolbar();
        setViewPager();
        return view;
    }

    private void setToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getString(R.string.tab_local_title_local));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        mToolbar.setPadding(0, CommonUtils.getStatusHeight(getActivity()), 0, 0);
    }

    private void setViewPager() {
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(new LocalSongFragment(), getString(R.string.song));
        adapter.addFragment(new LocalArtistFragment(), getString(R.string.artist));
        adapter.addFragment(new LocalAlbumFragment(), getString(R.string.ablum));
        mViewPager.setAdapter(adapter);
        mTab.setupWithViewPager(mViewPager);
        mTab.setTabTextColors(R.color.text_color,
                ThemeUtils.getThemeColorStateList(getActivity(), R.color.theme_color_primary).getDefaultColor());
        mTab.setSelectedTabIndicatorColor(ThemeUtils.getThemeColorStateList(getActivity(), R.color.theme_color_primary).getDefaultColor());

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mTitles = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
}
