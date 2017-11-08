package com.example.gheggie.virs;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static com.example.gheggie.virs.VirsUtils.currentPoet;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private static final int REQUEST_LOCATION = 0x01101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if we don't have it.
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            getCurrentUser();
            setupTabViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if user allows permission for the first time. show feed
        if(grantResults[0] == 0) {
            getCurrentUser();
            setupTabViews();
        }
    }

    private void getCurrentUser(){
        currentPoet = new Poet();
        database.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> fbUsernames = (Map<String, Object>) dataSnapshot.getValue();
                for(Map.Entry<String, Object> users : fbUsernames.entrySet()){
                    Map newUser = (Map)users.getValue();
                    if(newUser.get("userId").equals(currentUser.getUid())) {
                        currentPoet.setUserId(newUser.get("userId").toString());
                        currentPoet.setUsername(newUser.get("username").toString());
                        currentPoet.setPoems((ArrayList<String>) newUser.get("poems"));
                        currentPoet.setUserIcon(newUser.get("userIcon").toString());
                        currentPoet.setSnappedPoems((ArrayList<String>) newUser.get("snappedPoems"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupTabViews() {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.virsPurple));
        tabLayout.setTabTextColors(R.color.blackColor, R.color.virsPurple);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.bookpurple);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.calendarpurple);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.profilepurple);
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

    @Override
    public void onClick(View v) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        finish();
    }
}
