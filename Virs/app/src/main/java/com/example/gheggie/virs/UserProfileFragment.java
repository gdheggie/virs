package com.example.gheggie.virs;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    private ListView mUserPoems;
    private ListView mSnappedPoems;
    private TextView poemCount;
    private TextView snapCount;
    private Poet currentPoet;
    private ArrayList<String> poemIds = new ArrayList<>();
    private ArrayList<Poem> poems = new ArrayList<>();


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
        poemCount = (TextView)getActivity().findViewById(R.id.poems_count);
        snapCount = (TextView)getActivity().findViewById(R.id.snaps_count_text);
        currentPoet = VirsUtils.loadPoet(getActivity());
        poemCount.setText(String.valueOf(currentPoet.getPoems().size()));
        poemIds = currentPoet.getPoems();
        grabUserPoems();
    }

    private void grabUserPoems(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Poems");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> fbPoems = (Map<String, Object>) dataSnapshot.getChildren().iterator().next().getValue();

                for(Map.Entry<String, Object> poem : fbPoems.entrySet()) {
                    Map newPoem = (Map)poem.getValue();
                    if(poemIds.contains(newPoem.get("poemId").toString())) {
                        poems.add(new Poem(newPoem.get("title").toString(), newPoem.get("poem").toString()
                                , newPoem.get("poet").toString(), newPoem.get("date").toString()
                                , newPoem.get("poemId").toString()
                                , Integer.valueOf(newPoem.get("snapCount").toString())));
                        refreshUserList();
                        totalSnaps();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void totalSnaps(){
        int totalSnaps = 0;
        for(Poem p : poems) {
            totalSnaps += p.getSnapCount();
        }
        snapCount.setText(String.valueOf(totalSnaps));
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

    private void refreshUserList(){
        UserPoemAdapter userAdapter = new UserPoemAdapter(poems, getActivity());
        mUserPoems.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    private void refreshSnapList(){

    }
}
