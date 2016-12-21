package com.fade.drmmusic.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.bilibili.magicasakura.widgets.TintProgressBar;
import com.bumptech.glide.Glide;
import com.fade.drmmusic.App;
import com.fade.drmmusic.R;
import com.fade.drmmusic.core.MusicPlayer;
import com.fade.drmmusic.ui.activitys.MusicActivity;
import com.fade.drmmusic.ui.activitys.PlayingActivity;
import com.fade.drmmusic.ui.dialogs.PlayQueueFragment;
import com.fade.drmmusic.ui.interfaces.MusicStateListener;
import com.fade.drmmusic.utils.FLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/11/30.
 */

public class QuickControlFragment extends Fragment implements MusicStateListener {

    @BindView(R.id.playbar_img)
    ImageView mAlbumArt;

    @BindView(R.id.playbar_info)
    TextView mTitle;

    @BindView(R.id.playbar_singer)
    TextView mArtist;

    @BindView(R.id.play_list)
    TintImageView mPlayList;

    @BindView(R.id.control)
    TintImageView mPlayPause;

    @BindView(R.id.play_next)
    TintImageView mNext;

    @BindView(R.id.song_progress_normal)
    TintProgressBar mProgressBar;

    private boolean duetoplaypause = false;

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            mProgressBar.setMax((int) MusicPlayer.duration());
            mProgressBar.setProgress((int) position);

            if (MusicPlayer.isPlaying()) {
                mProgressBar.postDelayed(mUpdateProgress, 50);
            } else mProgressBar.removeCallbacks(this);

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quickcontrol, container, false);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.context, PlayingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.context.startActivity(intent);
            }
        });
        setOnClick();
        ((MusicActivity)getActivity()).addMusicStateListenerListener(this);
        return view;
    }

    private void setOnClick() {
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLog.i("点到了暂定播放");
                if (MusicPlayer.getQueueSize() == 0) {
                    Toast.makeText(App.context, getResources().getString(R.string.queue_is_empty),
                            Toast.LENGTH_SHORT).show();
                } else {
                    duetoplaypause = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MusicPlayer.playOrPause();
                        }
                    }, 60);
                }
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLog.i("点到了下一首");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.next();
                    }
                }, 60);

            }
        });
        
        mPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLog.i("点到了播放列表");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PlayQueueFragment playQueueFragment = new PlayQueueFragment();
                        playQueueFragment.show(getFragmentManager(), "playqueueframent");
                    }
                }, 60);
            }
        });

    }

    @Override
    public void onMetaChanged() {
        mTitle.setText(MusicPlayer.getTrackName());
        mArtist.setText(MusicPlayer.getArtistName());
        if (!duetoplaypause) {
            Glide.with(getContext())
                    .load(MusicPlayer.getAlbumPath())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_disk_210)
                    .into(mAlbumArt);
        }
        duetoplaypause = false;
        mPlayPause.setImageResource(MusicPlayer.isPlaying() ? R.drawable.playbar_btn_pause
                : R.drawable.playbar_btn_play);
        mPlayPause.setImageTintList(R.color.theme_color_primary);
        mProgressBar.setMax((int) MusicPlayer.duration());
        mProgressBar.postDelayed(mUpdateProgress, 10);
    }
}
