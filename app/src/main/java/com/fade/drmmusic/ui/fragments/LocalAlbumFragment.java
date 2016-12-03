package com.fade.drmmusic.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fade.drmmusic.R;
import com.fade.drmmusic.dataloaders.MusicInfoLoader;
import com.fade.drmmusic.models.AlbumInfo;
import com.fade.drmmusic.ui.adapters.AlbumAdapter;
import com.fade.drmmusic.widgets.DividerItemDecoration;
import com.fade.drmmusic.widgets.SongItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/2.
 */

public class LocalAlbumFragment extends Fragment {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private AlbumAdapter mAlbumAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview, container, false);
        ButterKnife.bind(this, view);
        setRecyclerView();
        reloadAdapter();
        return view;
    }

    private void reloadAdapter() {
        if (mAlbumAdapter == null) return;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                List<AlbumInfo> albumInfos = MusicInfoLoader.queryAllLocalAlbums(getActivity());
                mAlbumAdapter.updateDataSet(albumInfos);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                mAlbumAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void setRecyclerView() {
        mAlbumAdapter = new AlbumAdapter(getActivity(), null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAlbumAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SongItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }
}
