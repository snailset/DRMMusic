package com.fade.drmmusic.ui.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fade.drmmusic.R;
import com.fade.drmmusic.core.MusicPlayer;
import com.fade.drmmusic.models.MusicInfo;
import com.fade.drmmusic.ui.activitys.MusicActivity;
import com.fade.drmmusic.ui.adapters.PlaylistAdapter;
import com.fade.drmmusic.ui.interfaces.MusicStateListener;
import com.fade.drmmusic.utils.FLog;
import com.fade.drmmusic.widgets.DividerItemDecoration;
import com.fade.drmmusic.widgets.ScrollLinearLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by wm on 2016/2/4.
 */
public class PlayQueueFragment extends DialogFragment implements MusicStateListener {

    @BindView(R.id.playlist_addto)
    TextView mAdd;
    @BindView(R.id.play_list_number)
    TextView mPlaylistNum;
    @BindView(R.id.playlist_clear_all)
    TextView mClear;
    @BindView(R.id.play_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.scrollLinearLayout)
    ScrollLinearLayout mScrollLinearLayout;

    private PlaylistAdapter mAdapter;
    private ArrayList<MusicInfo> playlist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置无标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_queue, container);
        ButterKnife.bind(this, view);
        setClick();
        setRecyclerView();
        mScrollLinearLayout.setOnFinishScroll(new ScrollLinearLayout.OnFinishScrollLintener() {
            @Override
            public void onFinishScroll() {
                dismiss();
            }
        });
        new Load().execute();
        ((MusicActivity)getActivity()).addMusicStateListenerListener(this);
        return view;
    }

    private void setRecyclerView() {
        mAdapter = new PlaylistAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onStart() {
        super.onStart();
        int dialogHeight = (int) (getActivity().getResources().getDisplayMetrics().heightPixels * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }

    private void setClick() {
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLog.i("点击到了收藏列表");
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FLog.i("点击到了清空列表");
            }
        });
    }

    @Override
    public void onMetaChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    //异步加载recyclerview界面
    private class Load extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (getActivity() != null) {
//                 playlist = QueueLoader.getQueueSongs(getActivity());

                try {
                    Gson gson = new Gson();
                    FileInputStream fo = new FileInputStream(new File(getContext().getCacheDir().getAbsolutePath() + "playlist"));
                    String c = readTextFromSDcard(fo);
                    HashMap<Long, MusicInfo> play = gson.fromJson(c, new TypeToken<HashMap<Long, MusicInfo>>() {
                    }.getType());
                    if (play != null && play.size() > 0) {
                        long[] queue = MusicPlayer.getQueue();
                        int len = queue.length;
                        playlist = new ArrayList<>();

                        for (int i = 0; i < len; i++) {
                            playlist.add(play.get(queue[i]));
                        }
                        mAdapter.updateDataSet(playlist);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (playlist != null && playlist.size() > 0) {
                mPlaylistNum.setText("播放列表（" + playlist.size() + "）");
                mAdapter.notifyDataSetChanged();
                for (int i = 0; i < playlist.size(); i++) {
                    MusicInfo info = playlist.get(i);
                    if (info != null && MusicPlayer.getCurrentAudioId() == info.songId) {
                        mRecyclerView.scrollToPosition(i);
                    }
                }
            }

        }
    }
    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
