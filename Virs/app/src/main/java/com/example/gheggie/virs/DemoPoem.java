package com.example.gheggie.virs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DemoPoem extends AppCompatActivity {

    private TextView poemshow;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_poem);

        poemshow = (TextView)findViewById(R.id.poem_show);

        database = FirebaseDatabase.getInstance().getReference().child("demopoem");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                    Poem newPoem = new Poem(map.get("title"), map.get("poem"), 0);
                    Log.d("----------", map.get("poem"));
                    poemshow.setText(newPoem.getPoem());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
