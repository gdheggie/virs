package com.example.gheggie.virs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UsersProfileActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        Intent userIntent = getIntent();
        String userClickedId = userIntent.getStringExtra(VirsUtils.USER_CLICKED);

        if(userIntent.hasExtra(VirsUtils.USER_CLICKED)) {
            UserProfileFragment userProfile = UserProfileFragment.newInstance(userClickedId);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.user_frame,
                    userProfile,
                    UserProfileFragment.TAG
            ).commit();
        }
    }
}
