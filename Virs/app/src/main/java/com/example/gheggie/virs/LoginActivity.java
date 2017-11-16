package com.example.gheggie.virs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameText;
    private EditText passwordText;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TwitterLoginButton twitterSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Up UI
        Button signin = (Button)findViewById(R.id.sign_in_button);
        twitterSignIn = (TwitterLoginButton)findViewById(R.id.twitter_button);
        twitterSignIn.setEnabled(true);
        signin.setOnClickListener(this);
        usernameText = (EditText)findViewById(R.id.username_field);
        passwordText = (EditText)findViewById(R.id.password_field);
        TextView signUpText = (TextView) findViewById(R.id.sign_up_here);
        signUpText.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if(checkConnection()) {
            configTwitterSignIn();
        }

        if(firebaseAuth.getCurrentUser() != null) {
            // Start Poem Feed
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    private void configTwitterSignIn(){
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("8eSH8SWoMv68ELT2x79ovdHb6", "6VQ4ITJp9UQICsf6poGcwi1Xe8iQJ33TVipBYiz1LZTPlQBLnA"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        Twitter.getInstance();

        twitterSignIn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                AuthCredential credential = TwitterAuthProvider.getCredential(
                        result.data.getAuthToken().token,
                        result.data.getAuthToken().secret);
                firebaseAuth.signInWithCredential(credential).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        finish();
                        if(firebaseAuth.getCurrentUser() != null) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, EditActivity.class));
                        }
                    }
                }).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this
                                , e.getMessage()
                                , Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterSignIn.onActivityResult(requestCode, resultCode, data);
    }

    // check connection
    private boolean checkConnection() {
        ConnectivityManager mgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr != null) {
            NetworkInfo netInfo = mgr.getActiveNetworkInfo();
            if (netInfo != null) {
                if (netInfo.isConnected()) {
                    // if this is true, run registration
                    return true;
                }
            } else { // if there is no active connection
                Toast.makeText(
                        this, "Check connection",
                        Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(checkConnection()) {
            if (v.getId() == R.id.sign_in_button) { // sign user in
                userLogin();
            } else if (v.getId() == R.id.sign_up_here) { // send user to register screen
                RegisterFragment registerFrag = RegisterFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(
                        R.id.login_frame,
                        registerFrag,
                        RegisterFragment.TAG
                ).commit();
            }
        }
    }

    private void userLogin() {
        String userEmail = usernameText.getText().toString().trim();
        String userPassword = passwordText.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail)) {// if email field is empty, notify user
            usernameText.setError("Enter username");
        } else if (TextUtils.isEmpty(userPassword)) { // if password field is empty, notify user
            passwordText.setError("Enter a password");
        }else {
            //show progress dialog
            progressDialog.setMessage("Signing In...");
            progressDialog.show();

            //create user
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Start Poem Feed
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Login Unsuccessful",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
