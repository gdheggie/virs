package com.example.gheggie.virs;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterFragment extends Fragment{

    public static final String TAG = "RegisterFragment.TAG";
    private FirebaseAuth firebaseAuth;
    private EditText username2;
    private EditText passwordText2;
    private EditText confirmPasswordText;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        username2 = (EditText) getActivity().findViewById(R.id.username_field2);
        passwordText2 = (EditText)getActivity().findViewById(R.id.password_field2);
        confirmPasswordText = (EditText)getActivity().findViewById(R.id.re_password_field);
        TextView signInText = (TextView)getActivity().findViewById(R.id.sign_in_here);
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        Button registerButton = (Button)getActivity().findViewById(R.id.sign_up_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection()) {
                    userRegistration();
                }
            }
        });
    }

    // check connection
    private boolean checkConnection() {
        ConnectivityManager mgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr != null) {
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
            if (netInfo != null) {
                if (netInfo.isConnected()) {
                    // if this is true, run registration
                    return true;
                }
            } else { // if there is no active connection
                Toast.makeText(
                        getActivity(), "Check connection",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private void userRegistration() {
        String username = username2.getText().toString().trim();
        String userPassword = passwordText2.getText().toString().trim();
        String confirmPassword = confirmPasswordText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {// if email field is empty, notify user
            username2.setError("Enter username");
        } else if (TextUtils.isEmpty(userPassword)) { // if password field is empty, notify user
            passwordText2.setError("Enter a password");
        }  else if (!userPassword.equals(confirmPassword)){
            passwordText2.setText(null);
            confirmPasswordText.setText(null);
            Toast.makeText(getActivity(), "Passwords did not match", Toast.LENGTH_SHORT).show();
        }else {
            // save user to database & local storage
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                ArrayList<String> poems = new ArrayList<>();
                Poet newPoet = new Poet(username, user.getUid(),poems);
                databaseReference.child("Users").child(user.getUid()).push().setValue(newPoet);
                VirsUtils.savePoet(getActivity(), newPoet);
            }
            //show progress dialog
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            String usersignup = username + "@virs.com";

            //create user
            firebaseAuth.createUserWithEmailAndPassword(usersignup, userPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                //Go To Poem Feed
                                removeFragment();
                                startActivity(new Intent(getActivity(), PoemFeed.class));
                            } else {
                                Toast.makeText(getActivity(),
                                        "Registration Unsuccessful. Try Again!",
                                        Toast.LENGTH_SHORT).show();
                                username2.setText(null);
                                passwordText2.setText(null);
                                confirmPasswordText.setText(null);

                            }
                        }
                    });
        }
    }

    private void removeFragment(){
        getActivity().getFragmentManager().beginTransaction().
                remove(this).commit();
    }
}