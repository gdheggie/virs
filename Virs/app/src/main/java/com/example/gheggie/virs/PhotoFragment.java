package com.example.gheggie.virs;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhotoFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_PHOTO = 0x0111;
    public CircleImageView crop_view;
    private Poet newPoet;
    public static final String TAG = "PhotoFragment.TAG";
    private Uri mCropImageUri;
    private StorageReference fbStorage = FirebaseStorage.getInstance().getReference();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.photo_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        crop_view = (CircleImageView)getActivity().findViewById(R.id.cropped_view);
        Button selectButton = (Button)getActivity().findViewById(R.id.select_button);
        Button finishButton = (Button)getActivity().findViewById(R.id.crop_button);
        selectButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
    }

    private void saveUser(Uri url) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // save user to database & local storage
        if (user != null) {
            String userEmail = user.getEmail();
            if (userEmail != null) {
                    String[] username = userEmail.split("@");
                    ArrayList<Poem> poems = new ArrayList<>();
                    ArrayList<Poem> snappedPoems = new ArrayList<>();
                    newPoet = new Poet(username[0].toLowerCase(), user.getUid(), url, poems, snappedPoems);
                    databaseRef.child("Users").child(user.getUid()).setValue(newPoet);
                    VirsUtils.savePoet(getActivity(), newPoet);
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
            savePhoto();
        }
    }

    public void selectPicture() {
        getPermission();
    }

    private void getPermission() {
        //get permission if we do not have them yet
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permissions if we don't have it.
            ActivityCompat.requestPermissions(getActivity(),
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

    private void savePhoto(){
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setMessage("Saving user info...");
        progress.show();
        fbStorage.child("Photos").child(mCropImageUri.getLastPathSegment());
        fbStorage.putFile(mCropImageUri).addOnSuccessListener(
                getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progress.dismiss();
                saveUser(mCropImageUri);

            }
        });
//        removeFragment();
//        getActivity().finish();
//        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    private void removeFragment() {
        getActivity().getFragmentManager().beginTransaction().
                remove(this).commit();
    }
}
