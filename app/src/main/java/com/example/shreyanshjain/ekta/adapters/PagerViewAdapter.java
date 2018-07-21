package com.example.shreyanshjain.ekta.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.shreyanshjain.ekta.fragments.MainPageFragment;
import com.example.shreyanshjain.ekta.fragments.NotificationsFragment;

public class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MainPageFragment mainPageFragment = new MainPageFragment();
                return mainPageFragment;

            case 1:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
