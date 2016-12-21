package com.fade.drmmusic.ui.activitys;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fade.drmmusic.MediaAidlInterface;
import com.fade.drmmusic.core.MediaService;
import com.fade.drmmusic.core.MusicPlayer;
import com.fade.drmmusic.ui.interfaces.MusicStateListener;
import com.fade.drmmusic.utils.FLog;
import com.fade.drmmusic.utils.IConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.fade.drmmusic.core.MusicPlayer.mService;

/**
 * Created by SnailSet on 2016/11/29.
 */

public class MusicActivity extends AppCompatActivity implements ServiceConnection, MusicStateListener {


    private final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    private MusicPlayer.ServiceToken mToken;
    private PlaybackStatus mPlaybackStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = MusicPlayer.bindToService(this, this);
        mPlaybackStatus = new PlaybackStatus(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        IntentFilter f = new IntentFilter();
        f.addAction(MediaService.PLAYSTATE_CHANGED);
        f.addAction(MediaService.META_CHANGED);
        f.addAction(MediaService.QUEUE_CHANGED);
        f.addAction(IConstants.MUSIC_COUNT_CHANGED);
        f.addAction(MediaService.TRACK_PREPARED);
        f.addAction(MediaService.BUFFER_UP);
        f.addAction(IConstants.EMPTY_LIST);
        registerReceiver(mPlaybackStatus, new IntentFilter(f));
    }

    public void addMusicStateListenerListener(final MusicStateListener status) {
        FLog.i("addMusicStateListenerListener");
        if (status == this) {
            throw new UnsupportedOperationException("Override the method, don't add a listener");
        }

        if (status != null) {
            mMusicStateListener.add(status);
        }
    }

    public void removeMusicStateListenerListener(final MusicStateListener status) {
        if (status != null) {
            mMusicStateListener.remove(status);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }

        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }
        mMusicStateListener.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        onMetaChanged();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = MediaAidlInterface.Stub.asInterface(iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }

    @Override
    public void onMetaChanged() {
        FLog.i("onMetaChanged: change count: " + mMusicStateListener.size());
        for (final MusicStateListener listener : mMusicStateListener) {
            if (listener != null) {
                listener.onMetaChanged();
            }
        }
    }

    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<MusicActivity> mReference;


        public PlaybackStatus(final MusicActivity activity) {
            mReference = new WeakReference<>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            MusicActivity musicActivity = mReference.get();
            if (musicActivity != null) {
                if (action.equals(MediaService.META_CHANGED)) {
//                    musicActivity.updateTrackInfo();
                    musicActivity.onMetaChanged();
                } else if (action.equals(MediaService.PLAYSTATE_CHANGED)) {

                } else if (action.equals(MediaService.TRACK_PREPARED)) {
//                    musicActivity.updateTime();
                } else if (action.equals(MediaService.BUFFER_UP)) {
//                    musicActivity.updateBuffer(intent.getIntExtra("progress", 0));
                } else if (action.equals(IConstants.EMPTY_LIST)) {

                } else if (action.equals(MediaService.REFRESH)) {

                } else if (action.equals(IConstants.MUSIC_COUNT_CHANGED)) {
//                    musicActivity.refreshUI();
                } else if (action.equals(MediaService.PLAYLIST_CHANGED)) {

                } else if (action.equals(MediaService.QUEUE_CHANGED)) {
//                    musicActivity.updateQueue();
                } else if (action.equals(MediaService.TRACK_ERROR)) {
                    final String errorMsg = "错误";
                    Toast.makeText(musicActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
