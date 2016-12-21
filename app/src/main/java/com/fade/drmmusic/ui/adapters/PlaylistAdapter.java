package com.fade.drmmusic.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bilibili.magicasakura.widgets.TintImageView;
import com.fade.drmmusic.App;
import com.fade.drmmusic.R;
import com.fade.drmmusic.core.MusicPlayer;
import com.fade.drmmusic.models.MusicInfo;
import com.fade.drmmusic.utils.FLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/5.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHoler> {

    private ArrayList<MusicInfo> mMusicInfos;

    public PlaylistAdapter(ArrayList<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
    }

    public void updateDataSet(ArrayList<MusicInfo> musicInfos) {
        mMusicInfos = musicInfos;
    }

    @Override
    public ItemHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_playqueue_item, parent, false);
        return new ItemHoler(view);
    }

    @Override
    public void onBindViewHolder(ItemHoler holder, int position) {
        MusicInfo musicInfo = mMusicInfos.get(position);
        holder.mTitle.setText(musicInfo.musicName);
        holder.mArtist.setText(" -" + musicInfo.artist);
        if (MusicPlayer.getCurrentAudioId() == musicInfo.songId) {
            holder.mPlayState.setVisibility(View.VISIBLE);
            holder.mTitle.setTextColor(ThemeUtils.getThemeColorStateList(App.context, R.color.theme_color_primary).getDefaultColor());
            holder.mArtist.setTextColor(ThemeUtils.getThemeColorStateList(App.context, R.color.theme_color_primary).getDefaultColor());
            holder.mPlayState.setImageResource(R.drawable.song_play_icon);
            holder.mPlayState.setImageTintList(R.color.theme_color_primary);
        } else {
            holder.mPlayState.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMusicInfos == null ? 0 : mMusicInfos.size();
    }

    class ItemHoler extends RecyclerView.ViewHolder {

        @BindView(R.id.play_state)
        TintImageView mPlayState;
        @BindView(R.id.play_list_musicname)
        TextView mTitle;
        @BindView(R.id.play_list_artist)
        TextView mArtist;
        @BindView(R.id.play_list_delete)
        ImageView mDelete;

        public ItemHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FLog.i("点击了歌曲删除");
                }
            });
        }
    }
}
