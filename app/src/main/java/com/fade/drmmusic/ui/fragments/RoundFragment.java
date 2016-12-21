package com.fade.drmmusic.ui.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.fade.drmmusic.R;
import com.fade.drmmusic.ui.activitys.PlayingActivity;
import com.fade.drmmusic.utils.FLog;
import com.fade.drmmusic.widgets.SquareImageView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/10.
 */

public class RoundFragment extends Fragment {

    @BindView(R.id.sdv) public SquareImageView mSdv;
    private String mAlbumPath;
    private WeakReference<ObjectAnimator> mAnimatorWeakReference;
    private ObjectAnimator mAnimator;

    public static RoundFragment newInstance(String albumpath) {
        RoundFragment fragment = new RoundFragment();
        Bundle bundle = new Bundle();
        bundle.putString("album", albumpath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roundimage, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mAlbumPath = getArguments().getString("album");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLog.i("fragment 点击");
                ((PlayingActivity)getActivity()).toggleView();
            }
        });

        Glide.with(getActivity())
                .load(mAlbumPath)
                .centerCrop()
                .placeholder(R.drawable.placeholder_disk_play_song)
                .into(mSdv);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAnimatorWeakReference = new WeakReference(ObjectAnimator.ofFloat(getView(), "rotation", new float[]{0.0F, 360.0F}));
        mAnimator = mAnimatorWeakReference.get();
        mAnimator.setRepeatCount(Integer.MAX_VALUE);
        mAnimator.setDuration(25000L);
        mAnimator.setInterpolator(new LinearInterpolator());

        if (getView() != null)
            getView().setTag(R.id.tag_animator, mAnimator);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAnimator != null) {
            mAnimator = null;
        }
    }
}
