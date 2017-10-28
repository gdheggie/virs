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
import android.text.TextUtils;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.gheggie.virs.VirsUtils.currentPoet;

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
        savePhoto();
        // save user to database & local storage
        if (user != null) {
            if (TextUtils.isEmpty(username)) {
                mUsername.setError("Choose a username");
            } else if(finishButton.getText().equals("Finish")){
                if(currentPoet == null) {
                    ArrayList<String> poems = new ArrayList<>();
                    ArrayList<String> snappedPoems = new ArrayList<>();
                    Poet newPoet = new Poet(username.toLowerCase(), user.getUid(), profilePicture, poems, snappedPoems);
                    database.child("Users").child(user.getUid()).setValue(newPoet);
                    finish();
                    startActivity(new Intent(EditActivity.this, MainActivity.class));
                }else {
                    currentPoet.setUsername(username);
                    currentPoet.setUserIcon(profilePicture);
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

        if (currentPoet != null) {
            if (currentPoet.getUsername().equals(username)) {
                Drawable myIcon = getResources().getDrawable(android.R.drawable.checkbox_on_background, null);
                myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
                mUsername.setError("Available", myIcon);
                finishButton.setText(R.string.finish);
            }
        } else if(names.contains(username)) {
            mUsername.setError("Username already exists");
        } else {
            Drawable myIcon = getResources().getDrawable(android.R.drawable.checkbox_on_background, null);
            myIcon.setBounds(0, 0, myIcon.getIntrinsicWidth(), myIcon.getIntrinsicHeight());
            mUsername.setError("Available", myIcon);
            finishButton.setText(R.string.finish);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select_button2) {
            // open gallery
            selectPicture();
        } else if (v.getId() == R.id.check_finish) {
            grabUsernames();
        } else if (finishButton.getText() == "Finish") {
            saveUser();
        }
    }

    private void savePhoto() {
        if(mCropImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Photo...");
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
