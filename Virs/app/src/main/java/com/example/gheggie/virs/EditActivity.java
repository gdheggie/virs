package com.example.gheggie.virs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("VisibleForTests")
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
    private StorageReference mStorageRef;
    private String profilePicture;
    private ProgressDialog progressDialog;
    private Poet poet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activty);

        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        crop_view = (CircleImageView)findViewById(R.id.cropped_view);
        Button selectButton = (Button)findViewById(R.id.select_button2);
        ImageButton back = (ImageButton)findViewById(R.id.back_3);
        finishButton = (Button)findViewById(R.id.check_finish);
        mUsername = (TextView)findViewById(R.id.username_field2);
        selectButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        finishButton.setVisibility(View.INVISIBLE);
        editIntent = getIntent();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Photo...");

        if(user.getDisplayName() != null) {
            mUsername.setText(user.getDisplayName());
            back.setVisibility(View.GONE);
        }

        if(editIntent.hasExtra(VirsUtils.EDIT_PROFILE)) {
            poet = (Poet)editIntent.getSerializableExtra(VirsUtils.EDIT_PROFILE);
            mUsername.setText(poet.getUsername());
            if(!poet.getUserIcon().equals("")) {
                Picasso.with(this).load(poet.getUserIcon()).into(crop_view);
            }
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                grabUsernames();
            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                grabUsernames();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                grabUsernames();
            }

            @Override
            public void afterTextChanged(Editable s) {
                grabUsernames();
            }
        });
    }

    private void saveUser() {
        // save user to database
        if (user != null) {
            if (TextUtils.isEmpty(username)) {
                mUsername.setError("Choose a username");
            } else{
                if(poet == null) {
                    ArrayList<String> poems = new ArrayList<>();
                    ArrayList<String> snappedPoems = new ArrayList<>();
                    Poet newPoet = new Poet(username.toLowerCase(), user.getUid(), profilePicture, poems, snappedPoems);
                    database.child("Users").child(user.getUid()).setValue(newPoet);
                    finish();
                    startActivity(new Intent(EditActivity.this, MainActivity.class));
                }else {
                    poet.setUsername(username);
                    poet.setUserIcon(profilePicture);
                    database.child("Users").child(poet.getUserId()).removeValue();
                    database.child("Users").child(poet.getUserId()).setValue(poet);
                    editIntent.putExtra(VirsUtils.EDIT_PROFILE, poet);
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
                username = mUsername.getText().toString().trim();
                checkUsername(usernames, username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkUsername(ArrayList<String> names, String s){
        if (poet != null) {
            if (poet.getUsername().equals(s)) {
                Drawable myIcon = getResources().getDrawable(android.R.drawable.checkbox_on_background, null);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                mUsername.setError("Available", myIcon);
                finishButton.setVisibility(View.VISIBLE);
            }
        } else if(names.contains(s)) {
            mUsername.setError("Username already exists");
        } else {
            Drawable myIcon = getResources().getDrawable(android.R.drawable.checkbox_on_background, null);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            mUsername.setError("Available", myIcon);
            finishButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select_button2) {
            // open gallery
            selectPicture();
        } else if (v.getId() == R.id.check_finish) {
            grabUsernames();
            savePhoto();
        }
    }

    private void savePhoto() {
        if(mCropImageUri != null) {
            progressDialog.show();

            StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());
            ref.putFile(mCropImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            if(taskSnapshot != null) {
                                profilePicture = String.valueOf(taskSnapshot.getDownloadUrl());
                            }
                            saveUser();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progressNum = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading " + (int)progressNum + "%");
                        }
                    });

        }
        else {
            profilePicture = "";
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
            Intent pickPhoto = new Intent();
            pickPhoto.setType("image/*");
            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(pickPhoto , 1);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.cancel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == 0) {
            // open gallery
            Intent pickPhoto = new Intent();
            pickPhoto.setType("image/*");
            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(pickPhoto, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            mCropImageUri = data.getData();
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 4;
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mCropImageUri);
                crop_view.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
