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
import com.fade.drmmusic.models.AlbumInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/12/2.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ItemView> {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_LAST = 1;

    private List<AlbumInfo> mAlbumInfos;
    private Context mContext;

    public AlbumAdapter(Context context, List<AlbumInfo> albumInfos) {
        mContext = context;
        mAlbumInfos = albumInfos;
    }

    public void updateDataSet(List<AlbumInfo> albumInfos) {
        mAlbumInfos = albumInfos;
    }


    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_NORMAL:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
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
                AlbumInfo albumInfo = mAlbumInfos.get(position);
                holder.mAlbumName.setText(albumInfo.album_name);
                holder.mSongNum.setText(albumInfo.number_of_songs + "首 " + albumInfo.album_artist);
                holder.mMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2016/12/3 专辑更多
                    }
                });
                Glide.with(mContext)
                        .load(albumInfo.album_art)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_disk_210)
                        .into(holder.mArtistImage);
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
        return (mAlbumInfos == null || mAlbumInfos.size() == 0) ? 0 : mAlbumInfos.size()+1;
    }

    public class ItemView extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.artist_image)
        ImageView mArtistImage;

        @Nullable
        @BindView(R.id.album_name)
        TextView mAlbumName;

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
