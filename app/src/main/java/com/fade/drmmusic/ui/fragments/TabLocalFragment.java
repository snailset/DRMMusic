package com.fade.drmmusic.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.fade.drmmusic.R;
import com.fade.drmmusic.dataloaders.AlbumLoader;
import com.fade.drmmusic.dataloaders.ArtistLoader;
import com.fade.drmmusic.dataloaders.MusicInfoLoader;
import com.fade.drmmusic.dataloaders.SongLoader;
import com.fade.drmmusic.models.Album;
import com.fade.drmmusic.models.Artist;
import com.fade.drmmusic.models.MusicInfo;
import com.fade.drmmusic.models.Song;
import com.fade.drmmusic.ui.activitys.MainActivity;
import com.fade.drmmusic.ui.adapters.TabLocalAdapter;
import com.fade.drmmusic.ui.interfaces.ThemeChanger;
import com.fade.drmmusic.widgets.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/11/30.
 */

public class TabLocalFragment extends Fragment implements ThemeChanger{

    private Context mContext;
    private TabLocalAdapter mAdapter;

    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_local, container, false);
        ButterKnife.bind(this, view);
        themeChange();
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadAdapter();
            }
        });
        ((MainActivity) getActivity()).addThemeChanger(this);
        setRecyclerView();
//        doSomething();
        reloadAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        themeChange();
    }

    public void reloadAdapter() {
        if (mAdapter == null) return;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                mAdapter.updateDatas(generateMainItem());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }
        }.execute();
    }

    private void doSomething() {

        List<Song> songs = SongLoader.getAllSongs(mContext);
        List<Album> albums = AlbumLoader.getAllAlbums(mContext);
        List<Artist> artists = ArtistLoader.getAllArtists(mContext);
        List<MusicInfo> musicInfos = MusicInfoLoader.queryAllLocalMusics(mContext);
        System.out.println("----------- Song ----------");
        for (Song song : songs) {
            System.out.println(song);
        }

        System.out.println("----------- Album ----------");
        for (Album album : albums) {
            System.out.println(album);
        }

        System.out.println("----------- Artist ----------");
        for (Artist artist : artists) {
            System.out.println(artist);
        }

        System.out.println("----------- Artist ----------");
        for (MusicInfo musicInfo : musicInfos) {
            System.out.println(musicInfo);
        }

    }

    private void setRecyclerView() {
        mAdapter = new TabLocalAdapter(mContext, null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<TabLocalAdapter.MainItem> generateMainItem() {
        List<TabLocalAdapter.MainItem> mainItems = new ArrayList<>();
        int localMusicSize = MusicInfoLoader.queryAllLocalMusics(mContext).size();
        mainItems.add(new TabLocalAdapter.MainItem(getString(R.string.tab_local_title_local), localMusicSize, R.drawable.music_icn_local));
        mainItems.add(new TabLocalAdapter.MainItem(getString(R.string.tab_local_title_recent), 0, R.drawable.music_icn_recent));
        mainItems.add(new TabLocalAdapter.MainItem(getString(R.string.tab_local_title_download), 0, R.drawable.music_icn_dld));
        mainItems.add(new TabLocalAdapter.MainItem(getString(R.string.tab_local_title_myArtist), 0, R.drawable.music_icn_artist));
        return mainItems;
    }

    @Override
    public void themeChange() {
        mSwipeRefresh.setColorSchemeColors(ThemeUtils.getColorById(mContext, R.color.theme_color_primary));
    }
}
