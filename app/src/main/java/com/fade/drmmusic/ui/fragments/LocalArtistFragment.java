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
import com.fade.drmmusic.models.ArtistInfo;
import com.fade.drmmusic.ui.adapters.ArtistAdapter;
import com.fade.drmmusic.widgets.DividerItemDecoration;
import com.fade.drmmusic.widgets.SongItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/2.
 */

public class LocalArtistFragment extends Fragment {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private ArtistAdapter mArtistAdapter;

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
        if (mArtistAdapter == null) return;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                List<ArtistInfo> artistInfos = MusicInfoLoader.queryAllLocalArtists(getActivity());
                mArtistAdapter.updateDataSet(artistInfos);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mArtistAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void setRecyclerView() {
        mArtistAdapter = new ArtistAdapter(getActivity(), null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mArtistAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SongItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }
}
