package com.example.gheggie.virs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PHOTO = 0x011101;
    public CircleImageView crop_view;
    private Uri mCropImageUri;
    private StorageReference fbStorage;
    private Poet newPoet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        crop_view = (CircleImageView)findViewById(R.id.cropped_view);
        Button selectButton = (Button)findViewById(R.id.select_button);
        Button finishButton = (Button)findViewById(R.id.crop_button);
        selectButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        fbStorage = FirebaseStorage.getInstance().getReference();

    }

    private void saveUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // save user to database & local storage
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                    String[] username = userEmail.split("@");
                    ArrayList<String> poems = new ArrayList<>();
                    Poet newPoet = new Poet(username[0].toLowerCase(), user.getUid(), poems);
                    databaseRef.child("Users").child(user.getUid()).setValue(newPoet);
                    VirsUtils.savePoet(this, newPoet);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select_button) {
            // open gallery
            selectPicture();
        } else if (v.getId() == R.id.crop_button) {
            // crop photo
            if(crop_view != null) {
                saveUser();
            }
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
        startActivityForResult(pickPhoto , 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (requestCode == 0 || requestCode == 1) {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 4;
                mCropImageUri = data.getData();
                Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(mCropImageUri.toString()), 200, 200, false);
                crop_view.setImageBitmap(bmp);
                savePhoto(newPoet, mCropImageUri);
            }
        }
    }

    private void savePhoto(Poet poet, Uri uri){
        fbStorage.child("Photos").child(poet.getUserid());
        fbStorage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                finish();
                startActivity(new Intent(CropActivity.this, MainActivity.class));
            }
        });
    }
}
