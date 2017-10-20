package com.example.gheggie.virs;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private EditText search;
    private Toolbar searchBar;
    private Toolbar poetBar;
    private ImageButton liveStream;
    private ImageButton writePoem;
    private TextView poetName;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCurrentUser();
        searchBar =(Toolbar)findViewById(R.id.search_toolbar);
        poetBar = (Toolbar)findViewById(R.id.poet_screen_title);
        search = (EditText)findViewById(R.id.poem_search);
        liveStream = (ImageButton)findViewById(R.id.live_stream);
        writePoem = (ImageButton)findViewById(R.id.write_poem);
        ImageButton signOut = (ImageButton)findViewById(R.id.sign_out);
        poetName = (TextView)findViewById(R.id.poet_title);
        writePoem.setOnClickListener(mainActions);
        liveStream.setOnClickListener(mainActions);
        signOut.setOnClickListener(mainActions);
        setupTabViews();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void getCurrentUser(){
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
                        currentPoet.setSnappedPoems((ArrayList<String>) newUser.get("snappedPoems"));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private final View.OnClickListener mainActions = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.live_stream){
                // TODO: Go To Live Stream
            } else if (v.getId() == R.id.write_poem) {
                // Go To New Poem Screen
                startActivity(new Intent(MainActivity.this, NewPoemActivity.class));
            } else if (v.getId() == R.id.sign_out) {
                //sign user out
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        }
    };

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
                        search.setText(null);
                        search.setHint("Search Poems...");
                        searchBar.setVisibility(View.VISIBLE);
                        search.setVisibility(View.VISIBLE);
                        writePoem.setVisibility(View.VISIBLE);
                        liveStream.setVisibility(View.VISIBLE);
                        poetBar.setVisibility(View.GONE);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.calendarpurple);
                        searchBar.setVisibility(View.GONE);
                        poetBar.setVisibility(View.GONE);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.profilepurple);
                        searchBar.setVisibility(View.INVISIBLE);
                        poetBar.setVisibility(View.VISIBLE);
                        poetName.setText(currentPoet.getUsername());
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
