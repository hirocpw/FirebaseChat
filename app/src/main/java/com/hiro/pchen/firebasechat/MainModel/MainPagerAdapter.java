package com.hiro.pchen.firebasechat.MainModel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hiro.pchen.firebasechat.FriendsFragment;
import com.hiro.pchen.firebasechat.MessagesFragment;
import com.hiro.pchen.firebasechat.RequestsFragment;

/**
 * Created by ABB89 on 2017/9/11.
 */

class MainPagerAdapter extends FragmentPagerAdapter{
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) { //each position of each tab

        switch (position)
        {
            case 0:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 1:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "FRIENDS";
            case 1:
                return "REQUESTS";
            default:
                return null;
        }
    }
}
