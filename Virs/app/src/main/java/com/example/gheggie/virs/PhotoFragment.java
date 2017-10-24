package com.example.gheggie.virs;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class PhotoFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_PHOTO = 0x0111;
    public CircleImageView crop_view;
    public static final String TAG = "PhotoFragment.TAG";
    private TextView mUsername;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private ArrayList<String> usernames = new ArrayList<>();
    private String username;
    private Button finishButton;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        finishButton = (Button)getActivity().findViewById(R.id.crop_button);
        mUsername = (TextView)getActivity().findViewById(R.id.username_field2);
        selectButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
        finishButton.setText(R.string.check);

        if(user.getDisplayName() != null) {
            mUsername.setText(user.getDisplayName());
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
                ArrayList<String> poems = new ArrayList<>();
                ArrayList<String> snappedPoems = new ArrayList<>();
                Poet newPoet = new Poet(username.toLowerCase(), user.getUid(), poems, snappedPoems);
                database.child("Users").child(user.getUid()).setValue(newPoet);
                removeFragment();
                getActivity().finish();
                startActivity(new Intent(getActivity(), MainActivity.class));
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
        Log.d("USERNAMES", names.toString());
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
        if(v.getId() == R.id.select_button) {
            // open gallery
            selectPicture();
        } else if (v.getId() == R.id.crop_button) {
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
            Uri mCropImageUri = data.getData();
            crop_view.setImageURI(mCropImageUri);
        }
    }

//    private void savePhoto(){
//        final ProgressDialog progress = new ProgressDialog(getActivity());
//        progress.setMessage("Saving user info...");
//        progress.show();
//        progress.dismiss();
//        fbStorage.child("Photos").child(mCropImageUri.getLastPathSegment());
//        fbStorage.putFile(mCropImageUri).addOnSuccessListener(
//                getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                progress.dismiss();
//
//            }
//        });
//    }

    private void removeFragment() {
        getActivity().getFragmentManager().beginTransaction().
                remove(this).commit();
    }
}
