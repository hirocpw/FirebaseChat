package com.hiro.pchen.firebasechat.RegisterModel;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiro.pchen.firebasechat.MainModel.MainActivity;
import com.hiro.pchen.firebasechat.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;//Firebase code
    private TextInputLayout mUsername; //Declare variable of username
    private TextInputLayout mEmail;//Declare variable of email
    private TextInputLayout mPassword;//Declare variable of password
    private Button mCreateButt;//Declare variable of the create account button
    private Toolbar mToolbar; //Tool bar
    private ProgressDialog mRgisterProgress;//progress
    private DatabaseReference mDatabase; //firebase database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();//Firebase

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);   //set up toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create New Account"); //set up toolbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set up back button

        mRgisterProgress = new ProgressDialog(this);//initialise the progress animation

        mUsername = (TextInputLayout) findViewById(R.id.reg_username);
        mEmail =(TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateButt = (Button) findViewById(R.id.reg_create_butt);
        //Initialise the above variables

        mCreateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = mUsername.getEditText().getText().toString(); //get the texts in username field
                String user_email = mEmail.getEditText().getText().toString(); //get the texts in email field
                String user_pass = mPassword.getEditText().getText().toString(); //get the text in password field

                if (check_empty(user_name,user_email,user_pass)==true){ //if input check is OK

                    mRgisterProgress.setTitle("Registering..");
                    mRgisterProgress.setMessage("Please wait..");
                    mRgisterProgress.setCanceledOnTouchOutside(false);//if user touch something else, the progress still show
                    mRgisterProgress.show(); //show progress
                    register_user(user_name,user_email,user_pass);//call register_user function and pass the user_name, user_email and user_pass to the function
                }
            }
        });


    }

    private boolean check_empty(String user_name, String user_email, String user_pass )//Check if user input is empty or not
    {
        if (TextUtils.isEmpty(user_name)){ //if username is empty
            mUsername.setError("Please enter your Username"); //show error message
            return false;
        }
        else if (TextUtils.isEmpty(user_email)){ //if email is empty
            mEmail.setError("Please enter your Email"); //show error message
            return false;
        }
        else if (TextUtils.isEmpty(user_pass)){ //if password is empty
            mPassword.setError("Please enter your Password");//show error message
            return false;
        }

        else if(!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_pass)){ //if everything is entered
            mUsername.setError(null);
            mEmail.setError(null);
            mPassword.setError(null);
            //clear the error messages
            return true;
        }
        else
            return false;
    }


    private void register_user(final String user_name, final String user_email, String user_pass) {

        mAuth.createUserWithEmailAndPassword(user_email,user_pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() { //create an user account with name, email and password information
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid(); //get user uid in firebase
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid); //add childs into database by user uid
                            HashMap<String,String> userMap = new HashMap<>();//add database under "User"
                            userMap.put("name",user_name); //add user's username as the name that will be displayed
                            userMap.put("status","Hello, nice to meet you!"); //add the default message
                            userMap.put("image","default");//add the default link for the image
                            userMap.put("thumb_image","default");// store thumb image for users upload, because loading high resolution image may crash the app

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    //when the task is complete, if the registration process is successful
                                    mRgisterProgress.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_SHORT).show();//display successful message
                                    Log.d(TAG, "createUserWithEmail:success");//Tag log
                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(mainIntent);//Led the current page to the main page
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//prevent page go back to main page
                                    finish();
                                }
                            });

                        }
                        else {//when the task is complete, if the registration process is not successful
                            mRgisterProgress.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            //display error message and get exceptions
                        }
                    }

                });
    }
}
