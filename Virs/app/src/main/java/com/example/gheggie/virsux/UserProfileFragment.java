package com.example.gheggie.virsux;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.gheggie.virsux.VirsUtils.currentPoet;

public class UserProfileFragment extends Fragment implements View.OnClickListener {

    private ListView mUserPoems;
    private ListView mSnappedPoems;
    private TextView poemCount;
    private TextView snapCount;
    private Poet otherPoet = new Poet();
    private ArrayList<Poem> poems = new ArrayList<>();
    private ArrayList<Poem> snappedPoems = new ArrayList<>();
    private TextView userPoems;
    private TextView userSnapped;
    private ImageView poemLine;
    private ImageView snappedLine;
    public static final String TAG = "UserProfileFragment.TAG";
    private String userId;
    private Toolbar mUserBar;
    private TextView userName;
    private CircleImageView poetView;

    public static UserProfileFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString(VirsUtils.USER_ID, id);
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.userprofile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUserPoems = (ListView)getActivity().findViewById(R.id.user_poem_list);
        mUserPoems.setOnItemClickListener(listClick);
        mSnappedPoems = (ListView)getActivity().findViewById(R.id.user_snapped_list);
        mSnappedPoems.setOnItemClickListener(listClick);
        poemCount = (TextView)getActivity().findViewById(R.id.poems_count);
        snapCount = (TextView)getActivity().findViewById(R.id.snaps_count_text);
        userPoems = (TextView)getActivity().findViewById(R.id.poems_click);
        userSnapped = (TextView)getActivity().findViewById(R.id.snapped_click);
        userPoems.setOnClickListener(this);
        userSnapped.setOnClickListener(this);
        mUserBar = (Toolbar) getActivity().findViewById(R.id.user_bar);
        userName = (TextView) getActivity().findViewById(R.id.user_name);
        poetView = (CircleImageView)getActivity().findViewById(R.id.user_profile_image);

        ImageButton back = (ImageButton) getActivity().findViewById(R.id.back_2);
        ImageButton editProfile = (ImageButton)getActivity().findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(this);
        Intent userIntent = getActivity().getIntent();
        if(userIntent.hasExtra(VirsUtils.USER_CLICKED)){
            grabUserClicked();
            userId = getArguments().getString(VirsUtils.USER_ID);
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            editProfile.setVisibility(View.INVISIBLE);
        } else {
            setUserInfo();
            userName.setText(currentPoet.getUsername());
            if(!currentPoet.getUserIcon().equals("")) {
                Picasso.with(getActivity()).load(currentPoet.getUserIcon()).into(poetView);
            }
            back.setImageResource(R.drawable.logout);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertSignOut();
                }
            });
            editProfile.setVisibility(View.VISIBLE);
            if(currentPoet.getPoems() == null) {
                snapCount.setText("0");
            }
        }
        poemLine = (ImageView)getActivity().findViewById(R.id.line_view);
        snappedLine = (ImageView)getActivity().findViewById(R.id.line_view_2);

    }

    private void alertSignOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sign Out?");
        builder.setNegativeButton("NO", null);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        }).show();
    }

    private void setUserInfo(){
        if(currentPoet.getPoems() == null){
            poemCount.setText("0");
        } else {
            poemCount.setText(String.valueOf(currentPoet.getPoems().size()));
            grabUserPoems(currentPoet.getPoems());
        }

        if(currentPoet.getSnappedPoems() != null) {
            grabUserSnappedPoems(currentPoet.getSnappedPoems());
        } else {
            snapCount.setText("0");
        }
    }

    private final ListView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(parent.getId() == R.id.user_poem_list) {
                Intent poemIntent = new Intent(getActivity(), PoemActivity.class);
                poemIntent.putExtra(VirsUtils.USER_POEM, poems.get(position));
                startActivityForResult(poemIntent, 0);
            } else if (parent.getId() == R.id.user_snapped_list) {
                Intent poemIntent = new Intent(getActivity(), PoemActivity.class);
                poemIntent.putExtra(VirsUtils.SNAPPED_POEM, snappedPoems.get(position));
                startActivityForResult(poemIntent, 0);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0) {
                if (otherPoet.getUserId() != null) {
                    if (otherPoet.getPoems() != null) {
                        grabUserPoems(otherPoet.getPoems());
                        poemCount.setText(String.valueOf(otherPoet.getPoems().size()));
                    } else {
                        poems.clear();
                        refreshUserList();
                        poemCount.setText("0");
                        snapCount.setText("0");
                    }
                    if (otherPoet.getSnappedPoems() != null) {
                        grabUserSnappedPoems(otherPoet.getSnappedPoems());
                    } else {
                        snappedPoems.clear();
                        refreshSnapList();
                    }
                } else {
                    userName.setText(currentPoet.getUsername());
                    if (currentPoet.getPoems() != null) {
                        grabUserPoems(currentPoet.getPoems());
                        poemCount.setText(String.valueOf(currentPoet.getPoems().size()));
                    } else {
                        poems.clear();
                        refreshUserList();
                        poemCount.setText("0");
                        snapCount.setText("0");
                    }
                    if (currentPoet.getSnappedPoems() != null) {
                        grabUserSnappedPoems(currentPoet.getSnappedPoems());
                    } else {
                        snappedPoems.clear();
                        refreshSnapList();
                    }
                }
            }
    }

    private void grabUserClicked(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> fbUsers = (Map<String, Object>) dataSnapshot.getValue();
                for (Map.Entry<String, Object> user : fbUsers.entrySet()) {
                    Map newUser = (Map) user.getValue();
                    if (newUser.get("userId").toString().equals(userId)) {
                        otherPoet.setUsername(newUser.get("username").toString());
                        otherPoet.setUserId(newUser.get("userId").toString());
                        otherPoet.setPoems((ArrayList<String>) newUser.get("poems"));
                        otherPoet.setUserIcon(newUser.get("userIcon").toString());
                        otherPoet.setSnappedPoems((ArrayList<String>) newUser.get("snappedPoems"));
                        connectUserClicked(otherPoet);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void connectUserClicked(Poet poet){
        otherPoet = poet;
        if(otherPoet.getSnappedPoems() != null) {
            grabUserSnappedPoems(otherPoet.getSnappedPoems());
        }
        if(otherPoet.getPoems() != null) {
            grabUserPoems(otherPoet.getPoems());
            poemCount.setText(String.valueOf(otherPoet.getPoems().size()));
        } else {
            poemCount.setText("0");
            snapCount.setText("0");
        }

        if(!currentPoet.getUserIcon().equals("")) {
            Picasso.with(getActivity()).load(poet.getUserIcon()).into(poetView);
        }

        userName.setText(otherPoet.getUsername());
        mUserBar.setVisibility(View.VISIBLE);
    }

    private void grabUserPoems(final ArrayList<String> _list){
        poems.clear();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Poems");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> fbPoems = (Map<String, Object>) dataSnapshot.getChildren().iterator().next().getValue();

                for(Map.Entry<String, Object> poem : fbPoems.entrySet()) {
                    Map newPoem = (Map)poem.getValue();
                    if(_list.contains(newPoem.get("poemId").toString())) {
                        poems.add(new Poem(newPoem.get("title").toString(), newPoem.get("poem").toString()
                                , newPoem.get("poet").toString(), newPoem.get("date").toString()
                                , newPoem.get("poemId").toString(), newPoem.get("poetId").toString()
                                , newPoem.get("poetView").toString()
                                , Integer.valueOf(newPoem.get("snapCount").toString())));
                        refreshUserList();
                        totalSnaps(poems);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void grabUserSnappedPoems(final ArrayList<String> _list){
        snappedPoems.clear();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Poems");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> fbPoems = (Map<String, Object>) dataSnapshot.getChildren().iterator().next().getValue();

                for(Map.Entry<String, Object> poem : fbPoems.entrySet()) {
                    Map newPoem = (Map)poem.getValue();
                    if(_list.contains(newPoem.get("poemId").toString())) {
                        snappedPoems.add(new Poem(newPoem.get("title").toString(), newPoem.get("poem").toString()
                                , newPoem.get("poet").toString(), newPoem.get("date").toString()
                                , newPoem.get("poemId").toString(), newPoem.get("poetId").toString()
                                , newPoem.get("poetView").toString()
                                , Integer.valueOf(newPoem.get("snapCount").toString())));
                        refreshSnapList();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void totalSnaps(ArrayList<Poem> poemsList){
        int totalSnaps = 0;
        for (Poem p : poemsList) {
            totalSnaps += p.getSnapCount();
        }
        snapCount.setText(String.valueOf(totalSnaps));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.poems_click) {
            setUpTabImitator(true);
        } else if (v.getId() == R.id.snapped_click){
            setUpTabImitator(false);
        } else if (v.getId() == R.id.edit_profile){
            Intent editIntent = new Intent(getActivity(), EditActivity.class);
            editIntent.putExtra(VirsUtils.EDIT_PROFILE, currentPoet);
            startActivityForResult(editIntent, 0);
        }
    }

    private void setUpTabImitator(boolean userSide){
        if(userSide){
            // show user poems
            userPoems.setTextColor(ContextCompat.getColor(getActivity(), R.color.virsPurple));
            userPoems.setTypeface(null, Typeface.ITALIC);
            userSnapped.setTextColor(ContextCompat.getColor(getActivity(), R.color.blackColor));
            userSnapped.setTypeface(null, Typeface.NORMAL);
            poemLine.setVisibility(View.VISIBLE);
            snappedLine.setVisibility(View.INVISIBLE);
            mUserPoems.setVisibility(View.VISIBLE);
            mSnappedPoems.setVisibility(View.GONE);
            refreshUserList();
        } else {
            // show snapped poems
            userPoems.setTextColor(ContextCompat.getColor(getActivity(), R.color.blackColor));
            userPoems.setTypeface(null, Typeface.NORMAL);
            userSnapped.setTextColor(ContextCompat.getColor(getActivity(), R.color.virsPurple));
            userSnapped.setTypeface(null, Typeface.ITALIC);
            poemLine.setVisibility(View.INVISIBLE);
            snappedLine.setVisibility(View.VISIBLE);
            mUserPoems.setVisibility(View.GONE);
            mSnappedPoems.setVisibility(View.VISIBLE);
            refreshSnapList();
        }
    }

    private void refreshUserList() {
        CustomAdapter customAdapter = new CustomAdapter(poems, getActivity(), "User");
        mUserPoems.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    private void refreshSnapList(){
        CustomAdapter customAdapter = new CustomAdapter(snappedPoems, getActivity(), "Snapped");
        mSnappedPoems.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }
}
