package com.example.gheggie.virs;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class UserProfileFragment extends Fragment {

    private ListView mUserPoems;
    private ListView mSnappedPoems;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.userprofile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserPoems = (ListView)getActivity().findViewById(R.id.user_poem_list);
        mSnappedPoems = (ListView)getActivity().findViewById(R.id.user_snapped_list);
        setupUserProfileTab();
    }

    private void setupUserProfileTab() {
        final TabLayout tabLayout = (TabLayout)getActivity().findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), R.color.virsPurple));
        tabLayout.setTabTextColors(R.color.blackColor, R.color.virsPurple);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // show user poems
                        mUserPoems.setVisibility(View.VISIBLE);
                        mSnappedPoems.setVisibility(View.GONE);
                        break;
                    case 1:
                        // show snapped poems
                        mUserPoems.setVisibility(View.GONE);
                        mSnappedPoems.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.bookblack);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.calendarblack);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_account_circle_black_24dp);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
