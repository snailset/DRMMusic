package com.fade.drmmusic.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fade.drmmusic.R;
import com.fade.drmmusic.lastfmapi.LastFmClient;
import com.fade.drmmusic.lastfmapi.callbacks.ArtistInfoListener;
import com.fade.drmmusic.lastfmapi.models.ArtistQuery;
import com.fade.drmmusic.lastfmapi.models.LastfmArtist;
import com.fade.drmmusic.models.ArtistInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/2.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemView> {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LAST = 1;

    private List<ArtistInfo> mArtistInfos;
    private Context mContext;

    public ArtistAdapter(Context context, List<ArtistInfo> artistInfos) {
        mContext = context;
        mArtistInfos = artistInfos;
    }

    public void updateDataSet(List<ArtistInfo> artistInfos) {
        mArtistInfos = artistInfos;
    }


    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_NORMAL:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
                return new ItemView(v1);
            case TYPE_LAST:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
                return new ItemView(v2);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final ItemView holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NORMAL:
                ArtistInfo artistInfo = mArtistInfos.get(position);
                holder.mArtistName.setText(artistInfo.artist_name);
                holder.mSongNum.setText(artistInfo.number_of_tracks + "首");
                holder.mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2016/12/3 歌曲更多
                    }
                });
                //lastFm api加载歌手图片
                LastFmClient.getInstance(mContext).getArtistInfo(new ArtistQuery(artistInfo.artist_name), new ArtistInfoListener() {
                    @Override
                    public void artistInfoSucess(LastfmArtist artist) {
                        if (artist != null && artist.mArtwork != null) {
                            Glide.with(mContext)
                                    .load(artist.mArtwork.get(2).mUrl)
                                    .centerCrop()
                                    .placeholder(R.drawable.placeholder_disk_210)
                                    .into(holder.mArtistImage);
                        }
                    }

                    @Override
                    public void artistInfoFailed() {

                    }
                });
                break;
            case TYPE_LAST: // 最后一项，空的
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) return TYPE_LAST;
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return (mArtistInfos == null || mArtistInfos.size() == 0) ? 0 : mArtistInfos.size()+1;
    }

    public class ItemView extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.artist_image)
        ImageView mArtistImage;

        @Nullable
        @BindView(R.id.artist_name)
        TextView mArtistName;

        @Nullable
        @BindView(R.id.song_num)
        TextView mSongNum;

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
                        case TYPE_NORMAL:
                            //todo 这里写点击到歌手
                            break;
                        case TYPE_LAST: // 最后一项，空的
                            break;
                    }
                }
            });
        }
    }
}
