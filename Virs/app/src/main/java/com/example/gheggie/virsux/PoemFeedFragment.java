package com.example.gheggie.virsux;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PoemFeedFragment extends Fragment{

    private GridView poemGrid;
    private ArrayList<Poem> poems = new ArrayList<>();
    private ProgressBar poemProgress;
    private ArrayList<Poem> searchedPoems = new ArrayList<>();
    private EditText search;
    private ConstraintLayout layout;

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
        layout = (ConstraintLayout)getActivity().findViewById(R.id.poemFeed_background);
        Toolbar searchBar = (Toolbar) getActivity().findViewById(R.id.search_toolbar);
        searchBar.setVisibility(View.VISIBLE);
        search = (EditText) getActivity().findViewById(R.id.poem_search);
        ImageButton liveStream = (ImageButton) getActivity().findViewById(R.id.live_stream);
        ImageButton writePoem = (ImageButton) getActivity().findViewById(R.id.write_poem);
        writePoem.setOnClickListener(mainActions);
        liveStream.setOnClickListener(mainActions);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(!search.getText().toString().equals("")) {
                        searchPoems(search.getText().toString().toLowerCase());
                    } else {
                        checkConnection();
                        layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.feedBackground));
                    }
                    if (getActivity().getCurrentFocus() != null) {
                        InputMethodManager inputManager =
                                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private final View.OnClickListener mainActions = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.live_stream){
                // Go To Live Stream
                startActivity(new Intent(getActivity(), StreamActivity.class));
            } else if (v.getId() == R.id.write_poem) {
                // Go To New Poem Screen
                startActivity(new Intent(getActivity(), NewPoemActivity.class));
            }
        }
    };

    private void searchPoems(String ss){
        refreshSearchPoems(ss);
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
                     , newPoem.get("poetView").toString(), Integer.valueOf(newPoem.get("snapCount").toString())));
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

    private void refreshSearchPoems(String s){
        if(searchedPoems != null) {
            searchedPoems.clear();
        }
        for(Poem p : poems) {
            if(p.getTitle().toLowerCase().contains(s)) {
                searchedPoems.add(p);
            }
        }

        if(searchedPoems != null) {
            if(searchedPoems.size() == 0) {
                layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.whiteColor));
            }
        }

        CustomAdapter searchAdapter = new CustomAdapter(searchedPoems, getActivity(), "Snapped");
        poemGrid.setAdapter(searchAdapter);
        searchAdapter.notifyDataSetChanged();
    }

    // check connection them show feed if connection is on
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
                if(poems != null) {
                    refreshPoems();
                    Toast.makeText(
                            getActivity(), "Check connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
