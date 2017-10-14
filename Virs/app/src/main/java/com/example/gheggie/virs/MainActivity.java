package com.example.gheggie.virs;

import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private ArrayList<String> poems = new ArrayList<>();
    private Poet newPoet;
    private EditText search;
    private Toolbar searchBar;
    private Toolbar poetBar;
    private ImageButton liveStream;
    private ImageButton writePoem;
    private ImageButton signOut;
    private TextView poetName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        newPoet = VirsUtils.loadPoet(this);
        saveUser();
        searchBar =(Toolbar)findViewById(R.id.search_toolbar);
        poetBar = (Toolbar)findViewById(R.id.poet_screen_title);
        search = (EditText)findViewById(R.id.poem_search);
        liveStream = (ImageButton)findViewById(R.id.live_stream);
        writePoem = (ImageButton)findViewById(R.id.write_poem);
        signOut = (ImageButton)findViewById(R.id.sign_out);
        poetName = (TextView)findViewById(R.id.poet_title);
        writePoem.setOnClickListener(mainActions);
        liveStream.setOnClickListener(mainActions);
        signOut.setOnClickListener(mainActions);
        setupTabViews();
    }

    private void saveUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // save user to database & local storage
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                if(newPoet == null) {
                    String[] username = userEmail.split("@");
                    poems = new ArrayList<>();
                    newPoet = new Poet(username[0].toLowerCase(), user.getUid(), poems);
                    databaseRef.child("Users").child(user.getUid()).setValue(newPoet);
                    VirsUtils.savePoet(this, newPoet);
                }
            }
        }
    }

    private final View.OnClickListener mainActions = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.live_stream){
                // TODO: Go To Live Stream
            } else if (v.getId() == R.id.write_poem) {
                // TODO: Go To New Poem Screen
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
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.bookpurple);
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
                        searchBar.setVisibility(View.GONE);
                        poetBar.setVisibility(View.VISIBLE);
                        poetName.setText(newPoet.getUsername());
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
