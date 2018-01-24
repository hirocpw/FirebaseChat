package com.hiro.pchen.firebasechat.AccountModel;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiro.pchen.firebasechat.LoginModel.SignInActivity;
import com.hiro.pchen.firebasechat.R;

public class StatusActivity extends AppCompatActivity {

    //database and firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //layout
    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mStatusChangeButt;

    private ProgressDialog mProgress;//progress



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mProgress = new ProgressDialog(this);//initialise the progress animation

        mToolbar = (Toolbar) findViewById(R.id.status_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Your Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();//get current user
        String current_user_uid = mCurrentUser.getUid();//get user uid
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_uid);//get to the user database by current user uid

        mStatus = (TextInputLayout)findViewById(R.id.status_change_box);
        mStatusChangeButt = (Button) findViewById(R.id.status_comfirm_butt);

        mStatusChangeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = mStatus.getEditText().getText().toString();
                if(!TextUtils.isEmpty(status)) {
                    mProgress.setTitle("Signing In..");
                    mProgress.setMessage("Please wait..");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    //show progress animation
                    mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgress.dismiss();
                                Toast.makeText(StatusActivity.this, "Change Successful", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                mProgress.dismiss();
                                Toast.makeText(StatusActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
