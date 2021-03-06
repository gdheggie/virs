package com.example.gheggie.virs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.gheggie.virs.VirsUtils.currentPoet;

public class NewPoemActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText poemTitle;
    private EditText poemText;
    private Poem editPoem;
    private int snapCount;
    private String poemId;
    private String oldDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poem);
        setupToolBar();

        Intent editIntent = getIntent();
        Button previewButton = (Button)findViewById(R.id.preview_button);
        previewButton.setOnClickListener(this);
        poemTitle = (EditText)findViewById(R.id.title_field);
        poemText = (EditText)findViewById(R.id.poem_field);

        if (editIntent.hasExtra(VirsUtils.EDIT_POEM)) {
            editPoem = (Poem) editIntent.getSerializableExtra(VirsUtils.EDIT_POEM);
            poemTitle.setText(editPoem.getTitle());
            poemText.setText(editPoem.getPoem());
            snapCount = editPoem.getSnapCount();
            poemId = editPoem.getPoemId();
            oldDate = editPoem.getDate();

            poemTitle.setText(editPoem.getTitle());
            poemText.setText(editPoem.getPoem());
        } else if (editIntent.hasExtra(VirsUtils.NEW_POEM)) {
            editPoem = (Poem) editIntent.getSerializableExtra(VirsUtils.NEW_POEM);
            poemTitle.setText(editPoem.getTitle());
            poemText.setText(editPoem.getPoem());
            snapCount = editPoem.getSnapCount();
            poemId = editPoem.getPoemId();
            oldDate = editPoem.getDate();

            poemTitle.setText(editPoem.getTitle());
            poemText.setText(editPoem.getPoem());
        }
    }

    // set toolbar up
    private void setupToolBar() {
        Toolbar writerBar = (Toolbar) findViewById(R.id.new_poem_bar);
        setSupportActionBar(writerBar);
        writerBar.setNavigationIcon(R.drawable.arrow);
        writerBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.preview_button) {
            previewPoem();
        }
    }

    private void previewPoem(){
        String poemTitleText = poemTitle.getText().toString();
        String poem = poemText.getText().toString();

        if (TextUtils.isEmpty(poemTitleText)) {// if email field is empty, notify user
            poemTitle.setError("Give Your poem a title");
        } else if (TextUtils.isEmpty(poem)) { // if password field is empty, notify user
            poemText.setError("Enter a poem");
        }else {
            String myFormat = "MM/dd/yy-hh:mm:ss";
            String saveFormat = "MMddyyyyhhmmss";
            SimpleDateFormat savedFormat = new SimpleDateFormat(saveFormat, Locale.US);
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date date = new Date();
            String poemID = poemTitleText.trim() + savedFormat.format(date);
            if(editPoem != null) {
                editPoem = new Poem(poemTitleText, poem
                        , currentPoet.getUsername(), oldDate, poemId, currentPoet.getUserId()
                        , currentPoet.getUserIcon(), snapCount);
                // Go To Poem Screen with preview of Edited poem
                finish();
                Intent poemIntent = new Intent(NewPoemActivity.this, PoemActivity.class);
                poemIntent.putExtra(VirsUtils.EDIT_POEM, editPoem);
                startActivity(poemIntent);
            } else {
                Poem newPoem = new Poem(poemTitleText, poem
                        , currentPoet.getUsername(), sdf.format(date), poemID, currentPoet.getUserId()
                        , currentPoet.getUserIcon(), 0);
                // Go To Poem Screen with preview of poem
                finish();
                Intent poemIntent = new Intent(NewPoemActivity.this, PoemActivity.class);
                poemIntent.putExtra(VirsUtils.NEW_POEM, newPoem);
                startActivity(poemIntent);
            }
        }
    }
}
