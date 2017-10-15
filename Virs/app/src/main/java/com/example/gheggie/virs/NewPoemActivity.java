package com.example.gheggie.virs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewPoemActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText poemTitle;
    private EditText poemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poem);
        setupToolBar();

        Button previewButton = (Button)findViewById(R.id.preview_button);
        previewButton.setOnClickListener(this);
        poemTitle = (EditText)findViewById(R.id.title_field);
        poemText = (EditText)findViewById(R.id.poem_field);
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
            // Todo: Go To Poem Screen with preview of poem


            // ToDo: Uncomment when poem screen is setup: finish();
        }
    }
}
