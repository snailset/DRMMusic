package com.fade.drmmusic.ui.adapters;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.fade.drmmusic.R;
import com.fade.drmmusic.core.MusicPlayer;
import com.fade.drmmusic.dataloaders.MusicInfoLoader;
import com.fade.drmmusic.models.MusicInfo;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/2.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ItemView> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_LAST = 2;

    private List<MusicInfo> mMusicInfos;

    public SongAdapter(List<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
    }

    public void updateDataSet(List<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
    }


    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_HEADER:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.songlist_header, parent, false);
                return new ItemView(v0);
            case TYPE_NORMAL:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
                return new ItemView(v1);
            case TYPE_LAST:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
                return new ItemView(v2);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                holder.mSongNum.setText("(共"+mMusicInfos.size()+"首");
                holder.mManager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo 管理歌曲按钮的事件
                    }
                });
                break;
            case TYPE_NORMAL:
                MusicInfo musicInfo = mMusicInfos.get(position-1);
                holder.mSongTitle.setText(musicInfo.musicName);
                holder.mArtist.setText(musicInfo.artist);

                if (musicInfo.songId == MusicPlayer.getCurrentAudioId()) {
                    holder.mPlayState.setVisibility(View.VISIBLE);
                    holder.mPlayState.setImageResource(R.drawable.song_play_icon);
                    holder.mPlayState.setImageTintList(R.color.theme_color_primary);
                } else  {
                    holder.mPlayState.setVisibility(View.GONE);
                }
                holder.mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2016/12/3 歌曲更多
                    }
                });
                break;
            case TYPE_LAST: // 最后一项，空的
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        if (position == getItemCount()-1) return TYPE_LAST;
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return (mMusicInfos == null || mMusicInfos.size() == 0) ? 0 : mMusicInfos.size()+2;
    }

    public class ItemView extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.playlist_number)
        TextView mSongNum;

        @Nullable
        @BindView(R.id.manager)
        ImageView mManager;
        /* ---------------------------------- */
        @Nullable
        @BindView(R.id.play_state)
        TintImageView mPlayState;

        @Nullable
        @BindView(R.id.viewpager_list_toptext)
        TextView mSongTitle;

        @Nullable
        @BindView(R.id.viewpager_list_bottom_text)
        TextView mArtist;

        @Nullable
        @BindView(R.id.viewpager_list_button)
        ImageView mMore;

        public ItemView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (getItemViewType()) {
                        case TYPE_HEADER:
                            //todo 这里写点击播放全部
                            playAt(0);
                            break;
                        case TYPE_NORMAL:
                            //todo 这里写点击到谋一首歌曲
                            playAt(getAdapterPosition()-1);
                            break;
                        case TYPE_LAST: // 最后一项，空的
                            break;
                    }
                }

                private void playAt(final int position) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            long[] list = new long[mMusicInfos.size()];
//                            HashMap<Long, MusicInfo> infos = new HashMap();
//                            for (int i = 0; i < mMusicInfos.size(); i++) {
//                                MusicInfo info = mMusicInfos.get(i);
//                                list[i] = info.songId;
//                                info.islocal = true;
//                                info.albumData = MusicInfoLoader.getAlbumArtUri(info.albumId) + "";
//                                infos.put(list[i], mMusicInfos.get(i));
//                            }
//                            MusicPlayer.playAll(infos, list, position, false);
//                        }
//                    }).start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            long[] list = new long[mMusicInfos.size()];
                            HashMap<Long, MusicInfo> infos = new HashMap();
                            for (int i = 0; i < mMusicInfos.size(); i++) {
                                MusicInfo info = mMusicInfos.get(i);
                                list[i] = info.songId;
                                info.islocal = true;
                                info.albumData = MusicInfoLoader.getAlbumArtUri(info.albumId) + "";
                                infos.put(list[i], mMusicInfos.get(i));
                            }
                            MusicPlayer.playAll(infos, list, position, false);
                        }
                    }, 60); // TODO: 2016/12/5 要是不延迟60ms就会出现歌曲与歌曲信息不同步
                }
            });
        }
    }
}
