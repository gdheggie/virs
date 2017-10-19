package com.example.gheggie.virs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UsersProfileActivity extends AppCompatActivity implements UserClick {

    private String userClickedId;
    private Intent userIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        userIntent = getIntent();
        userClickedId = userIntent.getStringExtra(VirsUtils.USER_CLICKED);

        if(userIntent.hasExtra(VirsUtils.USER_CLICKED)) {
            UserProfileFragment userProfile = UserProfileFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.user_frame,
                    userProfile,
                    UserProfileFragment.TAG
            ).commit();
        }
    }

    @Override
    public String userClicked() {
        if(userIntent.hasExtra(VirsUtils.USER_CLICKED)) {
            return userClickedId;
        } else {
            return "";
        }

    }
}
