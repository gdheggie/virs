package com.example.gheggie.virs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserProfileFragment extends Fragment {

    public static final String TAG = "UserProfileFragment.TAG";

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.userprofile_fragment, container, false);
    }
}
