package com.fade.drmmusic.ui.adapters;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.fade.drmmusic.R;
import com.fade.drmmusic.ui.fragments.LocalMusicFragment;
import com.fade.drmmusic.utils.FLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SnailSet on 2016/11/30.
 */

public class TabLocalAdapter extends RecyclerView.Adapter<TabLocalAdapter.ItemView> {

    public static final int MAIN_ITEM = 0;

    private ArrayList mItems;
    private List<MainItem> mMainItems;
    private Context mContext;

    public TabLocalAdapter(Context context, List<MainItem> mainItems) {
        mContext = context;
        mItems = new ArrayList();
        mMainItems = mainItems;
        if (mainItems != null) {
            mItems.addAll(mMainItems);
        }
    }

    public void updateDatas(List<MainItem> mainItems) {
        if (mItems == null) {
            mItems = new ArrayList();
        }
        mItems.clear();
        mMainItems = mainItems;
        mItems.addAll(mMainItems);
    }
    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case MAIN_ITEM:
                View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_mainitem, parent, false);
                return new ItemView(v0);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {

        switch (getItemViewType(position)) {
            case MAIN_ITEM:  // 主菜单
                MainItem mainItem = (MainItem) mItems.get(position);
                holder.mMainItemImg.setImageResource(mainItem.img);
                holder.mMainItemImg.setImageTintList(R.color.theme_color_primary);
                holder.mMainItemTitle.setText(mainItem.title);
                holder.mMainItemCount.setText("("+mainItem.count+")");
                holder.itemView.setOnClickListener(new Listener(position));
                break;
        }
    }

    public class Listener implements View.OnClickListener {

        private int position;

        public Listener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (position) {
                case 0: //本地音乐
                    FLog.i("click 本地音乐");
                    gotoLocalMusicFragment();
                    break;
                case 1: //最近播放
                    FLog.i("click 最近播放");
                    break;
                case 2: //下载管理
                    FLog.i("click 下载管理");
                    break;
                case 3: //我的歌手
                    FLog.i("click 我的歌手");
                    break;
            }
        }
    }

    private void gotoLocalMusicFragment() {
        FLog.i("goto local music fragment");
        FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        LocalMusicFragment fragment = new LocalMusicFragment();
        transaction.hide(((AppCompatActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.fragment_container));
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null).commit();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof MainItem) {
            return MAIN_ITEM;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }


    public class ItemView extends RecyclerView.ViewHolder {

        @BindView(R.id.mainitem_img)
        TintImageView mMainItemImg;
        @BindView(R.id.mainitem_title)
        TextView mMainItemTitle;
        @BindView(R.id.mainitem_count)
        TextView mMainItemCount;
        public ItemView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * 表示本地音乐中的主item
     */
    public static class MainItem {
        public String title;
        public int count;
        public int img;

        public MainItem(String title, int count, int img) {
            this.title = title;
            this.count = count;
            this.img = img;
        }
    }

}
