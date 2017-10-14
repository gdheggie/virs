package com.example.gheggie.virs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int _NumOfTabs) {
        super(fm);
        mNumOfTabs = _NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PoemFeedFragment();
            case 1:
                return new EventFragment();
            case 2:
                return new UserProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
