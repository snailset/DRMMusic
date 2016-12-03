package com.fade.drmmusic.ui.activitys;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.fade.drmmusic.R;
import com.fade.drmmusic.ui.dialogs.CardPickerDialog;
import com.fade.drmmusic.ui.fragments.MainFragment;
import com.fade.drmmusic.ui.fragments.QuickControlFragment;
import com.fade.drmmusic.ui.interfaces.ThemeChanger;
import com.fade.drmmusic.utils.ThemeHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MusicActivity implements ThemeChanger {

    @BindView(R.id.drawable_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private List<ThemeChanger> mThemeChangers;

    public synchronized void addThemeChanger(ThemeChanger themeChanger) {
        if (mThemeChangers == null) {
            mThemeChangers = new ArrayList<>();
        }
        mThemeChangers.add(themeChanger);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setFragment();
        setDrawer();

    }

    private void setFragment() {
        Fragment fragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

        Fragment quickControlFragment = new QuickControlFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.quickcontrols_container, quickControlFragment).commitAllowingStateLoss();

//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                LocalMusicFragment fragment = new LocalMusicFragment();
//                transaction.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
//                transaction.add(R.id.fragment_container, fragment);
//                transaction.addToBackStack(null).commit();
//            }
//        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawer() {
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_msg:
                        break;
                    case R.id.nav_stop:
                        break;
                    case R.id.nav_night:
                        break;
                    case R.id.nav_tobeartist:
                        break;
                    case R.id.nav_skin:
                        gotoChangeSkin();
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_upload:
                        break;
                    case R.id.nav_about:
                        break;
                }
                return false;
            }
        });
    }


    /**
     * 换肤，打开换肤对话框
     */
    private void gotoChangeSkin() {
        CardPickerDialog cardPickerDialog = new CardPickerDialog();
        cardPickerDialog.setClickListener(new CardPickerDialog.ClickListener() {
            @Override
            public void onConfirm(int currentTheme) {
                if (ThemeHelper.getTheme(MainActivity.this) != currentTheme) {
                    ThemeHelper.setTheme(MainActivity.this, currentTheme);
                    ThemeUtils.refreshUI(MainActivity.this, new ThemeUtils.ExtraRefreshable() {
                                @Override
                                public void refreshGlobal(Activity activity) {
                                    //for global setting, just do once
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        final MainActivity context = MainActivity.this;
                                        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, null, ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                                        setTaskDescription(taskDescription);
                                        getWindow().setStatusBarColor(ThemeUtils.getColorById(context, R.color.theme_color_primary));
                                    }
                                }

                                @Override
                                public void refreshSpecificView(View view) {
                                    //TODO: will do this for each traversal
                                }
                            }
                    );
                    themeChange();
                }
            }
        });
        cardPickerDialog.show(getSupportFragmentManager(), "theme");
    }

    @Override
    public void themeChange() {
        if (mThemeChangers == null) return;
        for (ThemeChanger themeChanger : mThemeChangers) {
            themeChanger.themeChange();
        }
    }
}