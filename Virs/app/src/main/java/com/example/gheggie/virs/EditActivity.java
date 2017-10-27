package com.example.gheggie.virs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.gheggie.virs.VirsUtils.currentPoet;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PHOTO = 0x0111;
    public CircleImageView crop_view;
    private TextView mUsername;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> usernames = new ArrayList<>();
    private String username;
    private Button finishButton;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Uri mCropImageUri;
    private Intent editIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activty);
        crop_view = (CircleImageView)findViewById(R.id.cropped_view);
        Button selectButton = (Button)findViewById(R.id.select_button2);
        ImageButton back = (ImageButton)findViewById(R.id.back_3);
        finishButton = (Button)findViewById(R.id.check_finish);
        mUsername = (TextView)findViewById(R.id.username_field2);
        selectButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        finishButton.setText(R.string.check);
        editIntent = getIntent();

        if(user.getDisplayName() != null) {
            mUsername.setText(user.getDisplayName());
            back.setVisibility(View.GONE);
        }

        if(editIntent.hasExtra(VirsUtils.EDIT_PROFILE)) {
            Poet poet = (Poet)editIntent.getSerializableExtra(VirsUtils.EDIT_PROFILE);
            mUsername.setText(poet.getUsername());
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void saveUser() {
        username = mUsername.getText().toString().trim();
        grabUsernames();
        // save user to database & local storage
        if (user != null) {
            if (TextUtils.isEmpty(username)) {
                mUsername.setError("Choose a username");
            } else if(finishButton.getText().equals("Finish")){
                if(currentPoet == null) {
                    ArrayList<String> poems = new ArrayList<>();
                    ArrayList<String> snappedPoems = new ArrayList<>();
                    Poet newPoet = new Poet(username.toLowerCase(), user.getUid(), poems, snappedPoems);
                    database.child("Users").child(user.getUid()).setValue(newPoet);
                    finish();
                    startActivity(new Intent(EditActivity.this, MainActivity.class));
                }else {
                    currentPoet.setUsername(username);
                    database.child("Users").child(currentPoet.getUserId()).removeValue();
                    database.child("Users").child(currentPoet.getUserId()).setValue(currentPoet);
                    editIntent.putExtra(VirsUtils.EDIT_PROFILE, currentPoet);
                    finish();
                }
            }
        }
    }

    private void grabUsernames() {
        usernames.clear();
        database.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> fbUsernames = (Map<String, Object>) dataSnapshot.getValue();
                for(Map.Entry<String, Object> users : fbUsernames.entrySet()){
                    Map newUser = (Map)users.getValue();
                    usernames.add(newUser.get("username").toString());
                }
                checkUsername(usernames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkUsername(ArrayList<String> names){
        if(names.contains(username)) {
            mUsername.setError("Username already exists");
        } else {
            Drawable myIcon = getResources().getDrawable(android.R.drawable.checkbox_on_background, null);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            mUsername.setError("Avaliable", myIcon);
            finishButton.setText(R.string.finish);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select_button2) {
            // open gallery
            selectPicture();
        } else if (v.getId() == R.id.check_finish) {
            // crop photo
            //savePhoto();
            saveUser();
        }
    }

    public void selectPicture() {
        getPermission();
    }

    private void getPermission() {
        //get permission if we do not have them yet
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if we don't have it.
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PHOTO);
        } else {
            // open gallery
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // open gallery
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            mCropImageUri = data.getData();
            crop_view.setImageURI(mCropImageUri);
        }
    }
}
