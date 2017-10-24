package com.example.gheggie.virs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PoemFeedFragment extends Fragment {

    private GridView poemGrid;
    private ArrayList<Poem> poems = new ArrayList<>();
    private ProgressBar poemProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.poemfeed_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        poemGrid = (GridView)getActivity().findViewById(R.id.poem_grid);
        checkConnection();
        poemProgress = (ProgressBar)getActivity().findViewById(R.id.feed_progress);
        poemProgress.setVisibility(View.VISIBLE);
    }

    private void grabPoemFeed(){
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Poems");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                poems.clear();
                Map<String, Object> fbPoems = (Map<String, Object>) dataSnapshot.getChildren().iterator().next().getValue();
                poemProgress.setVisibility(View.GONE);
                for(Map.Entry<String, Object> poem : fbPoems.entrySet()) {
                    Map newPoem = (Map)poem.getValue();
                    poems.add(new Poem(newPoem.get("title").toString(), newPoem.get("poem").toString()
                    , newPoem.get("poet").toString(), newPoem.get("date").toString()
                    , newPoem.get("poemId").toString(), newPoem.get("poetId").toString()
                            , Integer.valueOf(newPoem.get("snapCount").toString())));
                    refreshPoems();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshPoems() {
        PoemFeedAdapter poemAdapter = new PoemFeedAdapter(poems, getActivity());
        poemGrid.setAdapter(poemAdapter);
        poemAdapter.notifyDataSetChanged();
    }

    // check connection
    private void checkConnection() {
        ConnectivityManager mgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr != null) {
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
            if (netInfo != null) {
                if (netInfo.isConnected()) {
                    // if this is true, run message feed
                    grabPoemFeed();
                }
            } else { // if there is no active connection
                Toast.makeText(
                        getActivity(), "Check connection",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
