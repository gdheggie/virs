package com.example.gheggie.virs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class PoemActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView poemTitle;
    private TextView poemPoet;
    private TextView thePoem;
    private ImageButton snap;
    private TextView snapCount;
    private ImageButton sharePoem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem);
        setupToolBar();

        poemTitle = (TextView)findViewById(R.id.title_name);
        poemPoet = (TextView)findViewById(R.id.by_text);
        thePoem = (TextView)findViewById(R.id.user_poem);
        snapCount = (TextView)findViewById(R.id.user_snaps);
        snap = (ImageButton)findViewById(R.id.poem_snap);
        snap.setTag(R.drawable.snap);
        sharePoem = (ImageButton)findViewById(R.id.poem_share);
        sharePoem.setTag(R.drawable.twittershare);
        showPoem();
    }

    // set toolbar up
    private void setupToolBar() {
        Toolbar poemBar = (Toolbar) findViewById(R.id.poem_bar);
        setSupportActionBar(poemBar);
        poemBar.setNavigationIcon(R.drawable.arrow);
        poemBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        }
    }

    private void showPoem(){
        // TODO: Get Poem and show here
    }

    private void updateSnapCount(){
        // TODO: Change snap count
    }

    private void shareToTwitter(){
        // TODO: Share to Twitter
    }
}
