package com.fade.drmmusic.ui.activitys;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fade.drmmusic.R;
import com.fade.drmmusic.core.MediaService;
import com.fade.drmmusic.core.MusicPlayer;
import com.fade.drmmusic.core.MusicTrack;
import com.fade.drmmusic.lrc.DefaultLrcParser;
import com.fade.drmmusic.lrc.LrcRow;
import com.fade.drmmusic.lrc.LrcView;
import com.fade.drmmusic.provider.PlaylistsManager;
import com.fade.drmmusic.ui.dialogs.PlayQueueFragment;
import com.fade.drmmusic.ui.fragments.RoundFragment;
import com.fade.drmmusic.ui.interfaces.MusicStateListener;
import com.fade.drmmusic.utils.FLog;
import com.fade.drmmusic.utils.IConstants;
import com.fade.drmmusic.utils.ImageUtils;
import com.fade.drmmusic.utils.MusicUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wm on 2016/2/21.
 */
public class PlayingActivity extends MusicActivity implements MusicStateListener {


    @BindView(R.id.albumArt) ImageView mAlbumArt; // 模糊的专辑封面
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.needle) ImageView mNeedle; // 磁头
    @BindView(R.id.headerView) FrameLayout mHeaderView; // 专辑内容
    @BindView(R.id.tragetlrc) TextView mTragetlrc; // 重试获取歌词
    @BindView(R.id.lrcview) LrcView mLrcview; // 歌词
    @BindView(R.id.lrcviewContainer) RelativeLayout mLrcviewContainer; //歌词容器
    @BindView(R.id.playing_fav) ImageView mPlayingFav; // 喜欢
    @BindView(R.id.playing_down) ImageView mPlayingDown; // 下载
    @BindView(R.id.playing_cmt) ImageView mPlayingCmt; // 评论
    @BindView(R.id.playing_more) ImageView mPlayingMore; // 更多
    @BindView(R.id.music_tool) LinearLayout mMusicTool; // 音乐工具栏
    @BindView(R.id.music_duration_played) TextView mMusicDurationPlayed; // 正在播放的位置
    @BindView(R.id.play_seek) SeekBar mPlaySeek; // 播放进度条
    @BindView(R.id.music_duration) TextView mMusicDuration; // 音乐长度
    @BindView(R.id.playing_mode) ImageView mPlayingMode; // 播放模式
    @BindView(R.id.playing_pre) ImageView mPlayingPre; // 上一首
    @BindView(R.id.playing_play) ImageView mPlayingPlay; // 暂停播放
    @BindView(R.id.playing_next) ImageView mPlayingNext; // 下一首
    @BindView(R.id.playing_playlist) ImageView mPlayingPlaylist; // 播放列表

    private ActionBar mActionBar;
    private ObjectAnimator mNeedleAnim, mAnimator;
    private boolean duetoplaypause;
    private String[] mAlbumPaths;
    private FragmentAdapter mFAdapter;
    private AnimatorSet mAnimatorSet;
    private boolean isNextOrPreSetPage = false; //判断viewpager由手动滑动 还是setcruuentitem换页
    private boolean isFav;
    private PlaylistsManager playlistsManager;
    private long bluredId = -1;
    private WeakReference<View> viewWeakReference;
    private View activeView;
    long time = -1;
    private boolean mDuetoScroll = false;
    private boolean mPageChangeByUser = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        playlistsManager = PlaylistsManager.getInstance(this);
        mAlbumPaths = MusicPlayer.getAlbumPathAll();
        ButterKnife.bind(this);
        setToolbar();
        loadOther();
        setViewPager();
        initLrcView();
    }

    private synchronized void refreshAnimator(final boolean start) {
        FLog.i("--- refreshAnimator");
        if (mAnimator != null) {
            mAnimator.setFloatValues(0);
            mAnimator.end();
        }
        final RoundFragment fragment = (RoundFragment) mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
        if (fragment.mSdv == null) {
            FLog.i("fragment.mSdv is null !!");
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (fragment.mSdv == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                mAnimator = ObjectAnimator.ofFloat(fragment.mSdv, "rotation", new float[]{0.0F, 360.0F});
                mAnimator.setRepeatCount(Integer.MAX_VALUE);
                mAnimator.setDuration(25000L);
                mAnimator.setInterpolator(new LinearInterpolator());
                FLog.i("refresh complete");
                if (start) {
                    FLog.i("refreshAnimator start");
                    mAnimatorSet = new AnimatorSet();
                    mAnimatorSet.play(mNeedleAnim).before(mAnimator);
                    mAnimatorSet.start();
                }
            }
        }.execute();
    }

    private void setViewPager() {
        mFAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFAdapter);
//        mViewPager.setPageTransformer(true, new PlaybarPagerTransformer());
        mViewPager.setCurrentItem(MusicPlayer.getQueuePosition() + 1);
        mViewPager.setOffscreenPageLimit(2);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mCurrentPosition;
            private Handler mHander = new Handler();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                FLog.i("+++ onPageSelected: " + position);
                mCurrentPosition = position;
                isNextOrPreSetPage = (position == 0 || position == mViewPager.getAdapter().getCount() - 1);

//                final int lastPosition = mViewPager.getAdapter().getCount() - 1;
//
//                if (!mPageChangeByUser) {  // 不是由于用户改变页面，可能是上一首，下一首，选择歌曲
//                    if (mCurrentPosition == 0) {
//                        FLog.i("+++ onPageSelected last: ");
//                        mViewPager.setCurrentItem(lastPosition - 1, false);
//                    } else if (mCurrentPosition == lastPosition) {
//                        FLog.i("+++ onPageSelected first: ");
//                        mViewPager.setCurrentItem(1, false);
//                    }
//                }

                if (!isNextOrPreSetPage) {
//                    mHander.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            refreshAnimator();
//                            if (MusicPlayer.isPlaying()) {
//                                mAnimatorSet = new AnimatorSet();
//                                FLog.i((float) mAnimator.getAnimatedValue() + "");
//                                mAnimatorSet.play(mNeedleAnim).before(mAnimator);
//                                mAnimatorSet.start();
//                            }
//                        }
//                    }, 800);
                    refreshAnimator(false);
                }
            }

            private Runnable pre = new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.previous(PlayingActivity.this, true);
                }
            };

            private Runnable next = new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.next();
                }
            };

            @Override
            public void onPageScrollStateChanged(int state) {
                boolean once = true;
                FLog.i("+++ state : " + state + " pos: " + mCurrentPosition);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    final int lastPosition = mViewPager.getAdapter().getCount() - 1;
                    if (mCurrentPosition == 0) {
//                        FLog.i("+++ last: ");
                        mViewPager.setCurrentItem(lastPosition - 1, false);
                        MusicPlayer.setQueuePosition(mAlbumPaths.length - 1);
                    } else if (mCurrentPosition == lastPosition) {
//                        FLog.i("+++ first: ");
                        mViewPager.setCurrentItem(1, false);
                        MusicPlayer.setQueuePosition(0);
                    } else {
//                        FLog.i("+++ else: ");
                        // todo 这里不能快速滑动。 如果连续滑动几下，只能使用setQueuePosition() 但是这样又浪费了很多性能
                        if (mCurrentPosition < MusicPlayer.getQueuePosition() + 1) {
//                            FLog.i("+++ pre: ");
                            mHander.postDelayed(pre, 200);
                        } else if (mCurrentPosition > MusicPlayer.getQueuePosition() + 1) {
//                            FLog.i("+++ next: ");
                            mHander.postDelayed(next, 200);
                        } else {  // 没改变，继续动画
                            if (MusicPlayer.isPlaying()) {
                                if (mAnimator != null && !mAnimator.isRunning() && mNeedleAnim != null && !mNeedleAnim.isRunning()
                                        && mAnimatorSet != null && !mAnimatorSet.isRunning()) {
                                    FLog.i("onPageScrollStateChanged running");
                                    mAnimatorSet = new AnimatorSet();
                                    mAnimatorSet.play(mNeedleAnim).before(mAnimator);
                                    mAnimatorSet.start();
                                }
                            }
                        }
                    }
                    mDuetoScroll = true;
                    once = true;
                } else {
                    mHander.removeCallbacks(pre);
                    mHander.removeCallbacks(next);
                    if (state == ViewPager.SCROLL_STATE_DRAGGING && once) {  // 拖动的时候要暂定动画
                        FLog.i("暂停动画");
                        if (mNeedleAnim != null) {
                            mNeedleAnim.reverse();
                            mNeedleAnim.end();
                        }
                        if (mAnimator != null ) {
                            mAnimator.cancel();
                            float valueAvatar = (float) mAnimator.getAnimatedValue();
                            mAnimator.setFloatValues(valueAvatar, 360f + valueAvatar);
                        }
                        once = false;
                    }
                }


            }
        });
    }

    private void initLrcView() {
        mLrcview.setOnSeekToListener(new LrcView.OnSeekToListener() {
            @Override
            public void onSeekTo(int progress) {
                MusicPlayer.seek(progress);
            }
        });
        mLrcview.setOnLrcClickListener(new LrcView.OnLrcClickListener() {
            @Override
            public void onClick() {
                toggleView();
            }
        });
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadOther() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mNeedleAnim = ObjectAnimator.ofFloat(mNeedle, "rotation", -30, 0);
                mNeedleAnim.setDuration(300);
                mNeedleAnim.setRepeatMode(0);
                mNeedleAnim.setInterpolator(new LinearInterpolator());
                setSeekBarListener();
            }
        }).start();
    }

    private void setSeekBarListener() {
        if (mPlaySeek != null) {
            mPlaySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                    i = (int) (i * MusicPlayer.duration() / 100);
                    mLrcview.seekTo(i, true, b);
                    if (b) {
                        MusicPlayer.seek((long) i);
                        mMusicDurationPlayed.setText(MusicUtils.makeShortTimeString(PlayingActivity.this.getApplication(), i / 1000));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }


    @OnClick({
            R.id.playing_fav,
            R.id.playing_more,
            R.id.playing_mode,
            R.id.playing_pre,
            R.id.playing_play,
            R.id.playing_next,
            R.id.headerView,
            R.id.tragetlrc,
            R.id.lrcviewContainer,
            R.id.playing_playlist})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_fav:
                FLog.i("点到了喜欢");
                break;
            case R.id.playing_more:
                FLog.i("点到了更多");
                break;
            case R.id.playing_mode:
                MusicPlayer.cycleRepeat();
                updatePlaymode();
                break;
            case R.id.playing_pre:
                mViewPager.setCurrentItem(MusicPlayer.getQueuePosition(), false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.previous(PlayingActivity.this.getApplication(), true);
                    }
                }, 200);

                break;
            case R.id.playing_play:
                duetoplaypause = true;
                if (MusicPlayer.isPlaying()) {
                    mPlayingPlay.setImageResource(R.drawable.play_rdi_btn_play);
                } else {
                    mPlayingPlay.setImageResource(R.drawable.play_rdi_btn_pause);
                }
                if (MusicPlayer.getQueueSize() != 0) {
                    MusicPlayer.playOrPause();
                }
                break;
            case R.id.playing_next:
                mViewPager.setCurrentItem(MusicPlayer.getQueuePosition() + 2, false);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.next();
                    }
                }, 200);
                break;
            case R.id.playing_playlist:
                PlayQueueFragment playQueueFragment = new PlayQueueFragment();
                playQueueFragment.show(getSupportFragmentManager(), "playlistframent");
                break;
            case R.id.headerView:
                FLog.i("headerview 点击");
                toggleView();
                break;
            case R.id.tragetlrc:
                Toast.makeText(getApplicationContext(), R.string.click_try, Toast.LENGTH_SHORT).show();
                break;
            case R.id.lrcviewContainer:
                toggleView();
                break;
        }
    }

    private void updatePlaymode() {
        if (MusicPlayer.getShuffleMode() == MediaService.SHUFFLE_NORMAL) {
            mPlayingMode.setImageResource(R.drawable.play_icn_shuffle);
            Toast.makeText(PlayingActivity.this.getApplication(), getResources().getString(R.string.random_play),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            switch (MusicPlayer.getRepeatMode()) {
                case MediaService.REPEAT_ALL:
                    mPlayingMode.setImageResource(R.drawable.play_icn_loop);
                    Toast.makeText(PlayingActivity.this.getApplication(), getResources().getString(R.string.loop_play),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MediaService.REPEAT_CURRENT:
                    mPlayingMode.setImageResource(R.drawable.play_icn_one);
                    Toast.makeText(PlayingActivity.this.getApplication(), getResources().getString(R.string.play_one),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void toggleView() {
        mLrcviewContainer.setVisibility(mLrcviewContainer.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        mHeaderView.setVisibility(mHeaderView.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        mMusicTool.setVisibility(mMusicTool.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
    }

    class FragmentAdapter extends FragmentStatePagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return RoundFragment.newInstance(mAlbumPaths[mAlbumPaths.length - 1]);
            }
            if (position == mAlbumPaths.length + 1) {
                return RoundFragment.newInstance(mAlbumPaths[0]);
            }
            return RoundFragment.newInstance(mAlbumPaths[position - 1]);
        }

        @Override
        public int getCount() {
            return mAlbumPaths.length + 2;
        }
    }

    public class PlaybarPagerTransformer implements ViewPager.PageTransformer {


        /**
         * @param view
         * @param position 0表示 当前view位于中间 1表示view位于右侧。-1表示view位于左侧
         */
        @Override
        public void transformPage(View view, float position) {

            if (position == 0) {
//                FLog.i("+++++++++a " + position);
                if (MusicPlayer.isPlaying()) {
//                    mAnimator = (ObjectAnimator) view.getTag(R.id.tag_animator);
//                    if (mAnimator != null && !mAnimator.isRunning() && !isNextOrPreSetPage) {
//                        FLog.i("tran running");
//                        mAnimatorSet = new AnimatorSet();
//                        mAnimatorSet.play(mNeedleAnim).before(mAnimator);9
//                        mAnimatorSet.start();
//                    }
//                    if (mAnimator != null && !mAnimator.isRunning() && mNeedleAnim != null && !mNeedleAnim.isRunning()
//                            && mAnimatorSet != null && !mAnimatorSet.isRunning()) {
//                        FLog.i("tran running");
//                        mAnimatorSet = new AnimatorSet();
//                        mAnimatorSet.play(mNeedleAnim).before(mAnimator);
//                        mAnimatorSet.start();
//                    }
                }

            } else if (position == -1 || position == 1) {
//                FLog.i("+++++++++b " + position);
                /*ObjectAnimator animator = (ObjectAnimator) view.getTag(R.id.tag_animator);
                if (animator != null) {
                    animator.setFloatValues(0);
                    animator.end();
                    animator = null;
                }*/
            } else if (position == -2 || position == 2) {
                return;
            } else {
//                FLog.i("+++++++++c " + position);
                /*if (mNeedleAnim != null && !mNeedleAnim.isRunning()) {
                    mNeedleAnim.reverse();
                    mNeedleAnim.end();
                }

                mAnimator = (ObjectAnimator) view.getTag(R.id.tag_animator);
                if (mAnimator != null) {
                    mAnimator.cancel();
                    float valueAvatar = (float) mAnimator.getAnimatedValue();
                    mAnimator.setFloatValues(valueAvatar, 360f + valueAvatar);
                }*/
            }
        }
    }

    private Bitmap mBitmap;

    @Override
    public void onMetaChanged() {
        if (time > 0 && time + 300 > System.currentTimeMillis()) {
            return;
        }
        time = System.currentTimeMillis();
        if (MusicPlayer.getQueueSize() == 0) return;

        System.out.println("============ PlayingActivity onMateChanged ==============");
        if (!duetoplaypause) {
            isFav = false;
            ArrayList<MusicTrack> favlists = playlistsManager.getPlaylist(IConstants.FAV_PLAYLIST);
            for (int i = 0; i < favlists.size(); i++) {
                if (MusicPlayer.getCurrentAudioId() == favlists.get(i).mId) {
                    isFav = true;
                    break;
                }
            }
            updateFav(isFav);
            updateLrc();
            if (MusicPlayer.getCurrentAudioId() != bluredId) {

                FLog.i("ablum path: " + MusicPlayer.getAlbumPath());

                Glide.with(this)
                        .load(MusicPlayer.getAlbumPath())
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                FLog.i("------- onResourceReady --------");
                                mBitmap = resource;
                                new BlurredAlbumArt().execute();
                            }
                        });


            }
            bluredId = MusicPlayer.getCurrentAudioId();
        }
        duetoplaypause = false;

        if (MusicPlayer.getQueuePosition() + 1 != mViewPager.getCurrentItem() && !mDuetoScroll) {
            FLog.i("setting position");
            mViewPager.setCurrentItem(MusicPlayer.getQueuePosition() + 1, false);
        }
        mDuetoScroll = false;


        mActionBar.setTitle(MusicPlayer.getTrackName());
        mActionBar.setSubtitle(MusicPlayer.getArtistName());
        mMusicDuration.setText(MusicUtils.makeShortTimeString(PlayingActivity.this.getApplication(), MusicPlayer.duration() / 1000));
        mPlaySeek.postDelayed(mUpdateProgress, 10);

        if (MusicPlayer.isPlaying()) {
            mPlayingPlay.setImageResource(R.drawable.play_rdi_btn_pause);

        } else {
            mPlayingPlay.setImageResource(R.drawable.play_rdi_btn_play);
        }

//        mAnimatorSet = new AnimatorSet();




        mAlbumPaths = MusicPlayer.getAlbumPathAll();
        mFAdapter.notifyDataSetChanged();

        if (mAnimator == null) { //第一次打开
            refreshAnimator(MusicPlayer.isPlaying() ? true : false);
        } else {
            gotoAnim();
        }

    }

    private void gotoAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MusicPlayer.isPlaying()) {
                    if (mAnimator != null && !mAnimator.isRunning() && mNeedleAnim != null && !mNeedleAnim.isRunning()) {
                        FLog.i("meta running");
                        mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.play(mNeedleAnim).before(mAnimator);
                        mAnimatorSet.start();
                    }

                } else {
                    if (mNeedleAnim != null && !mNeedleAnim.isRunning()) {
                        FLog.i("meta reverse");
                        mNeedleAnim.reverse();
                        mNeedleAnim.end();
                    }

                    if (mAnimator != null && mAnimator.isRunning()) {
                        mAnimator.cancel();
                        float valueAvatar = (float) mAnimator.getAnimatedValue();
                        mAnimator.setFloatValues(valueAvatar, 360f + valueAvatar);
                    }
                }
            }
        }, 200);
    }

    private Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            if (mPlaySeek != null) {
                long position = MusicPlayer.position();
                long duration = MusicPlayer.duration();
                if (duration > 0)
                    mPlaySeek.setProgress((int) (mPlaySeek.getMax() * position / duration));
                mMusicDurationPlayed.setText(MusicUtils.makeShortTimeString(PlayingActivity.this.getApplication(), position / 1000));
            }

            if (MusicPlayer.isPlaying()) {
                mPlaySeek.postDelayed(mUpdateProgress, 100);
            } else {
                mPlaySeek.removeCallbacks(this);
            }
        }
    };

    private void updateLrc() {
        List<LrcRow> lrcRows = getLrcRows();
        if (lrcRows != null && lrcRows.size() > 0) {
            mTragetlrc.setVisibility(View.INVISIBLE);
            mLrcview.setLrcRows(lrcRows);
        } else {
            mTragetlrc.setVisibility(View.VISIBLE);
            mLrcview.reset();
        }
    }

    private List<LrcRow> getLrcRows() {

        List<LrcRow> rows = null;
        InputStream is = null;
        try {
            is = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/drmmusic/lrc/" + MusicPlayer.getCurrentAudioId());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is == null) {
                return null;
            }
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            // System.out.println(sb.toString());
            rows = DefaultLrcParser.getIstance().getLrcRows(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private void updateFav(boolean b) {
        if (b == true) {
            mPlayingFav.setImageResource(R.drawable.play_icn_loved);
        } else {
            mPlayingFav.setImageResource(R.drawable.play_rdi_icn_love);
        }
    }

    private class BlurredAlbumArt extends AsyncTask<Void, Void, Drawable> {
        @Override
        protected Drawable doInBackground(Void... params) {
            Drawable drawable = ImageUtils.createBlurredImageFromBitmap(mBitmap, PlayingActivity.this.getApplication(), 12);
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                if (mAlbumArt.getDrawable() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{mAlbumArt.getDrawable(), drawable});
                    mAlbumArt.setImageDrawable(td);
                    //去除过度绘制
                    td.setCrossFadeEnabled(true);
                    td.startTransition(370);

                } else {
                    mAlbumArt.setImageDrawable(drawable);
                }
//                mAlbumArt.setImageResource(R.drawable.login_bg_night);
            }
        }
    }
}
