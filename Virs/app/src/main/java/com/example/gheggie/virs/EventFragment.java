package com.example.gheggie.virs;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EventFragment extends Fragment {

    public static final String TAG = "EventFragment.TAG";
    public static EventFragment newInstance() {
        return new EventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_fragment, container, false);
    }
}
