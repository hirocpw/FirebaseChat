package com.hiro.pchen.firebasechat.LoginModel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hiro.pchen.firebasechat.MainModel.MainActivity;
import com.hiro.pchen.firebasechat.R;

public class SignInActivity extends AppCompatActivity {

    private Toolbar mToolbar; //Tool bar
    private TextInputLayout mSignInEmail; //Declare the variable of sign in email
    private TextInputLayout mSignInPass; //Declare the variable of sign in password
    private Button mSignInButt; //Delcare variable of the button
    private ProgressDialog mSigInProgress; //Progress animation
    private FirebaseAuth mAuth; //Firebase code
    private static final String TAG = "LogInEmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();//Firebase code

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);   //set up toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log In");//set current tool bar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // set the back button

        mSigInProgress = new ProgressDialog(this); //initialise the progress animation

        mSignInEmail = (TextInputLayout) findViewById(R.id.sign_in_email);
        mSignInPass = (TextInputLayout) findViewById(R.id.sign_in_password);
        mSignInButt = (Button) findViewById(R.id.sign_in_butt);
        //initialise the email, password, and button

        mSignInButt.setOnClickListener(new View.OnClickListener() { //set a click listener for sign in button
            @Override
            public void onClick(View view) {

                String email = mSignInEmail.getEditText().getText().toString(); //get user email input
                String pass = mSignInPass.getEditText().getText().toString();//get user password input

                if (!TextUtils.isEmpty(email)||!TextUtils.isEmpty(pass)) //if the input fields are not empty
                {
                    mSignInEmail.setError(null);
                    mSignInPass.setError(null);
                    //clear the error messages
                    mSigInProgress.setTitle("Signing In..");
                    mSigInProgress.setMessage("Please wait..");
                    mSigInProgress.setCanceledOnTouchOutside(false);
                    mSigInProgress.show();
                    //show progress animation
                    SignInUser(email,pass);
                    //call SignInUser
                }
                else //if input fields are not empty
                {
                    mSignInEmail.setError("Please Check your email");
                    mSignInPass.setError("Please Check your password");
                    //show error messages
                }

            }
        });

    }

    private void SignInUser(String email, String pass) //sign in the users
    {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //user firebase functions to sign user in
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) { //when the task is complete

                if(task.isSuccessful()) //if the sign in task is successful
                {
                    mSigInProgress.dismiss();
                    Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();//display successful message
                    Log.d(TAG, "LogInEmailPassword:success");//Tag log
                    Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//prevent page go back to main page
                    startActivity(mainIntent);
                    //go to main activity
                    finish();
                }
                else {//when the task is complete, if the registration process is not successful
                    mSigInProgress.dismiss();
                    Log.w(TAG, "LogInEmailPassword:failure", task.getException());
                    Toast.makeText(SignInActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                    //display error message and get exceptions
                }

            }
        });


    }
}
