package com.example.gheggie.virs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.gheggie.virs.VirsUtils.currentPoet;

public class PoemActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView poemTitle;
    private TextView poemPoet;
    private TextView thePoem;
    private ImageButton snap;
    private TextView snapCount;
    private TextView shareLabel;
    private TextView poemDate;
    private ImageButton sharePoem;
    private Intent poemIntent;
    private Poem newPoem = null;
    private ImageButton deletePoemButton;
    private Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem);
        setupToolBar();
        poemTitle = (TextView) findViewById(R.id.title_name);
        poemPoet = (TextView) findViewById(R.id.by_text);
        thePoem = (TextView) findViewById(R.id.user_poem);
        snapCount = (TextView) findViewById(R.id.user_snaps);
        shareLabel = (TextView) findViewById(R.id.share_label);
        poemDate = (TextView) findViewById(R.id.when_text);
        snap = (ImageButton) findViewById(R.id.poem_snap);
        sharePoem = (ImageButton) findViewById(R.id.poem_share);
        poemIntent = getIntent();
        snapCount.setOnClickListener(this);
        snap.setOnClickListener(this);
        shareLabel.setOnClickListener(this);
        sharePoem.setOnClickListener(this);
        snap.setTag(R.drawable.snap);
        sharePoem.setTag(R.drawable.twittershare);

        upload = (Button) findViewById(R.id.upload_poem);
        upload.setOnClickListener(this);
        if (poemIntent.hasExtra(VirsUtils.NEW_POEM)) {
            upload.setVisibility(View.VISIBLE);
        } else {
            upload.setVisibility(View.GONE);
        }
        showPoem();

        if(newPoem.getPoetId().equals(currentPoet.getUserId())) {
            snap.setClickable(false);
            snapCount.setClickable(false);
        }

        setUpSnapCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        poemIntent = data;
        showPoem();
        upload.setVisibility(View.VISIBLE);
        upload.setText("Finish");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPoem(newPoem);
                finish();
            }
        });
    }

    private void setUpSnapCount(){
        if(currentPoet.getSnappedPoems() != null) {
            if (!currentPoet.getSnappedPoems().contains(newPoem.getPoemId())) {
                snap.setTag(R.drawable.snap);
                snap.setImageResource(R.drawable.snap);
            } else {
                snap.setTag(R.drawable.snapped);
                snap.setImageResource(R.drawable.snapped);
            }
        }
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
                if(poemIntent.hasExtra(VirsUtils.NEW_POEM)) {
                    Intent editIntent = new Intent(PoemActivity.this, NewPoemActivity.class);
                    editIntent.putExtra(VirsUtils.NEW_POEM, newPoem);
                    startActivity(editIntent);
                }

            }
        });
        deletePoemButton = (ImageButton)findViewById(R.id.delete_poem_2);
        deletePoemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePoemAlert(newPoem);
            }
        });
    }

    // click listener for snap and share
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.poem_snap || v.getId() == R.id.user_snaps) {
            VirsUtils.snapChange(this, snap, snapCount);
            updateSnapCount();
        } else if (v.getId() == R.id.poem_share || v.getId() == R.id.share_label) {
            VirsUtils.shareChange(this, sharePoem, shareLabel);
            shareToTwitter();
        } else if (v.getId() == R.id.upload_poem && upload.getText().equals("Upload Poem")) {
            uploadPoem(newPoem);
            finish();
        } else if(upload.getText().equals("Edit Poem")){
            Intent editIntent = new Intent(PoemActivity.this, NewPoemActivity.class);
            editIntent.putExtra(VirsUtils.EDIT_POEM, newPoem);
            startActivityForResult(editIntent, 0);
        }
    }

    private void showPoem(){
        //show poem based on intent
        if(poemIntent.hasExtra(VirsUtils.NEW_POEM)) {
            newPoem = (Poem)poemIntent.getSerializableExtra(VirsUtils.NEW_POEM);
            populatePoem(newPoem);
        } else if (poemIntent.hasExtra(VirsUtils.FEED_POEM)){
            // show clicked poem
            newPoem = (Poem)poemIntent.getSerializableExtra(VirsUtils.FEED_POEM);
            populatePoem(newPoem);
        } else if (poemIntent.hasExtra(VirsUtils.USER_POEM)) {
            // show clicked poem
            newPoem = (Poem)poemIntent.getSerializableExtra(VirsUtils.USER_POEM);
            populatePoem(newPoem);
        } else if (poemIntent.hasExtra(VirsUtils.SNAPPED_POEM)) {
            // show clicked poem
            newPoem = (Poem)poemIntent.getSerializableExtra(VirsUtils.SNAPPED_POEM);
            populatePoem(newPoem);
        } else if (poemIntent.hasExtra(VirsUtils.EDIT_POEM)){
            // show edited poem
            newPoem = (Poem)poemIntent.getSerializableExtra(VirsUtils.EDIT_POEM);
            populatePoem(newPoem);
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

        // show delete icon & Edit for user owned poems
        if(currentPoet.getUserId().equals(poem.getPoetId())) {
            deletePoemButton.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
            upload.setText(R.string.edit_poem);
        } else {
            deletePoemButton.setVisibility(View.GONE);
        }
    }

    private void updateSnapCount() {
        // change snap count
        int snapNumber = newPoem.getSnapCount();
        if (currentPoet.getSnappedPoems() != null) {
            if (!currentPoet.getSnappedPoems().contains(newPoem.getPoemId())) {
                snapNumber += 1;
                newPoem.setSnapCount(snapNumber);
                currentPoet.getSnappedPoems().add(newPoem.getPoemId());
                addSnappedPoem(newPoem);
                snapCount.setText(String.valueOf(newPoem.getSnapCount()));
                if (newPoem.getSnapCount() == 1) {
                    String snaps = newPoem.getSnapCount() + " snap";
                    snapCount.setText(snaps);
                } else {
                    String snaps = newPoem.getSnapCount() + " snaps";
                    snapCount.setText(snaps);
                }
            } else {
                snapNumber -= 1;
                newPoem.setSnapCount(snapNumber);
                currentPoet.getSnappedPoems().remove(newPoem.getPoemId());
                removeSnappedPoem(newPoem);
                snapCount.setText(String.valueOf(newPoem.getSnapCount()));
                if (newPoem.getSnapCount() == 1) {
                    String snaps = newPoem.getSnapCount() + " snap";
                    snapCount.setText(snaps);
                } else {
                    String snaps = newPoem.getSnapCount() + " snaps";
                    snapCount.setText(snaps);
                }
            }

        } else {
            ArrayList<String> snappedList = new ArrayList<>();
            snappedList.add(newPoem.getPoemId());
            currentPoet.setSnappedPoems(snappedList);
            snapNumber = newPoem.getSnapCount();
            snapNumber += 1;
            newPoem.setSnapCount(snapNumber);
            if (newPoem.getSnapCount() == 1) {
                String snaps = newPoem.getSnapCount() + " snap";
                snapCount.setText(snaps);
            } else {
                String snaps = newPoem.getSnapCount() + " snaps";
                snapCount.setText(snaps);
            }
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Poems").child(newPoem.getPoemId()).removeValue();
        database.child("Poems").child(newPoem.getPoemId()).setValue(newPoem);
        database.child("Users").child(currentPoet.getUserId()).removeValue();
        database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);
    }

    private void shareToTwitter(){
        // TODO: Share to Twitter
    }

    // add poem to snapped list
    private void addSnappedPoem(Poem poem) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        if(currentPoet.getSnappedPoems() == null) {
            ArrayList<String> snappedPoems = new ArrayList<>();
            snappedPoems.add(poem.getPoemId());
            currentPoet.setSnappedPoems(snappedPoems);
        } else {
            currentPoet.getSnappedPoems().add(poem.getPoemId());
        }

        database.child("Users").child(currentPoet.getUserId()).removeValue();
        database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);
    }

    // remove poem from snapped list
    private void removeSnappedPoem(Poem poem) {
        DatabaseReference database =
                FirebaseDatabase.getInstance().getReference();

        currentPoet.getSnappedPoems().remove(poem.getPoemId());

        database.child("Users").child(currentPoet.getUserId()).removeValue();
        database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);
    }

    //Save Poem to Database
    private void uploadPoem(Poem poem){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Poems").child(poem.getPoemId()).setValue(poem);

        if(currentPoet.getPoems() == null) {
            ArrayList<String> poems = new ArrayList<>();
            poems.add(poem.getPoemId());
            currentPoet.setPoems(poems);
        } else {
            currentPoet.getPoems().add(poem.getPoemId());
        }

        database.child("Users").child(currentPoet.getUserId()).removeValue();
        database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);
    }

    private void deletePoemAlert(final Poem poem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + poem.getTitle());
        builder.setNegativeButton("NO", null);
        builder.setPositiveButton("DELETE POEM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserPoem(poem);
            }
        }).show();
    }

    //Delete Poem from Database
    private void deleteUserPoem(Poem poem){
        DatabaseReference database =
                FirebaseDatabase.getInstance().getReference();
        database.child("Poems").child(poem.getPoemId()).removeValue();
        currentPoet.getPoems().remove(poem.getPoemId());

        database.child("Users").child(currentPoet.getUserId()).removeValue();
        database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);

        finish();
    }

}
