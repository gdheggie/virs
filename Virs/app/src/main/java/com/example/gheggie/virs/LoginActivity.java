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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameText;
    private EditText passwordText;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set Up UI
        Button signin = (Button)findViewById(R.id.sign_in_button);
        Button twitterSignIn = (Button)findViewById(R.id.twitter_button);
        signin.setOnClickListener(this);
        twitterSignIn.setOnClickListener(this);
        usernameText = (EditText)findViewById(R.id.username_field);
        passwordText = (EditText)findViewById(R.id.password_field);
        TextView signUpText = (TextView) findViewById(R.id.sign_up_here);
        signUpText.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            // Start Poem Feed
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
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
                getFragmentManager().beginTransaction().replace(
                        R.id.login_frame,
                        registerFrag,
                        RegisterFragment.TAG
                ).commit();
            } else if (v.getId() == R.id.twitter_button) { // sign user in with twitter
                // TODO: Sign In With Twitter
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
                                        "Login Failed. Try Again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
