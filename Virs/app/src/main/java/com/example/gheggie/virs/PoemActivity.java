package com.example.gheggie.virs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PoemActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView poemTitle;
    private TextView poemPoet;
    private TextView thePoem;
    private ImageButton snap;
    private TextView snapCount;
    private TextView poemDate;
    private ImageButton sharePoem;
    private Intent poemIntent;
    private Poet currentPoet;
    private Poem newPoem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem);
        setupToolBar();

        currentPoet = VirsUtils.loadPoet(this);
        poemTitle = (TextView)findViewById(R.id.title_name);
        poemPoet = (TextView)findViewById(R.id.by_text);
        thePoem = (TextView)findViewById(R.id.user_poem);
        snapCount = (TextView)findViewById(R.id.user_snaps);
        poemDate = (TextView)findViewById(R.id.when_text);
        snap = (ImageButton)findViewById(R.id.poem_snap);
        snap.setTag(R.drawable.snap);
        sharePoem = (ImageButton)findViewById(R.id.poem_share);
        sharePoem.setTag(R.drawable.twittershare);
        poemIntent = getIntent();

        Button upload = (Button)findViewById(R.id.upload_poem);
        upload.setOnClickListener(this);
        if(poemIntent.hasExtra(VirsUtils.NEW_POEM)) {
            upload.setVisibility(View.VISIBLE);
        } else {
            upload.setVisibility(View.GONE);
        }
        showPoem();
    }

    // set toolbar up
    private void setupToolBar() {
        Toolbar poemBar = (Toolbar) findViewById(R.id.poem_bar);
        setSupportActionBar(poemBar);
        ImageButton back = (ImageButton)findViewById(R.id.back_poem);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent poemIntent = new Intent(PoemActivity.this, NewPoemActivity.class);
                poemIntent.putExtra(VirsUtils.NEW_POEM, newPoem);
                startActivity(poemIntent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.poem_snap) {
            VirsUtils.snapChange(snap);
            updateSnapCount();
        } else if (v.getId() == R.id.poem_share) {
            VirsUtils.shareChange(sharePoem);
            shareToTwitter();
        } else if (v.getId() == R.id.upload_poem) {
            uploadPoem(newPoem);
            finish();
        }
    }

    private void showPoem(){
        //Get Poem and show here
        if(poemIntent.hasExtra(VirsUtils.NEW_POEM)) {
            newPoem = (Poem)poemIntent.getSerializableExtra(VirsUtils.NEW_POEM);
            populatePoem(newPoem);
        } else {
            // ToDo: Grab Poem from database;
        }
    }

    private void populatePoem(Poem poem){
        // add correct poem data to corresponding fields.
        poemTitle.setText(poem.getTitle());
        String byWho = "by: " + poem.getPoet();
        poemPoet.setText(byWho);
        String[] poemBirth = poem.getDate().split("-");
        String poemCreated = "created: " + poemBirth[0];
        poemDate.setText(poemCreated);
        thePoem.setText(poem.getPoem());
        if(poem.getSnapCount() == 1) {
            String snaps = poem.getSnapCount() + " snap";
            snapCount.setText(snaps);
        } else {
            String snaps = poem.getSnapCount() + " snaps";
            snapCount.setText(snaps);
        }
    }

    private void updateSnapCount(){
        // TODO: Change snap count
    }

    private void shareToTwitter(){
        // TODO: Share to Twitter
    }

    private void uploadPoem(Poem poem){
        //Save Poem to Database
        DatabaseReference database =
                FirebaseDatabase.getInstance().getReference();
        database.child("Poems").child(poem.getPoemId()).setValue(poem);

        currentPoet.getPoems().add(poem.getPoemId());
        VirsUtils.savePoet(this, currentPoet);

        database.child("Users").child(currentPoet.getUserId()).removeValue();
        database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);
    }
}
