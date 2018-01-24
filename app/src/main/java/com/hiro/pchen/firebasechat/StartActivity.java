package com.hiro.pchen.firebasechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hiro.pchen.firebasechat.LoginModel.SignInActivity;
import com.hiro.pchen.firebasechat.RegisterModel.RegisterActivity;

public class StartActivity extends AppCompatActivity {

    private Button mJoinButton; //declare the join button
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mJoinButton = (Button) findViewById(R.id.reg_join_us);
        mJoinButton.setOnClickListener(new View.OnClickListener() { //set up on click listener for join button
            @Override
            public void onClick(View view) { //hen user click the join button
                Intent join_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(join_intent); //send user to the registration page
               // finish();
            }
        });

        mSignInButton = (Button) findViewById(R.id.reg_sign_in);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign_in_intent = new Intent(StartActivity.this, SignInActivity.class);
                startActivity(sign_in_intent);
            }
        });


    }
}
