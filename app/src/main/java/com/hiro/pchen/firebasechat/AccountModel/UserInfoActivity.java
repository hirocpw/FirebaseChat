package com.hiro.pchen.firebasechat.AccountModel;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiro.pchen.firebasechat.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mUserInfoName;
    private TextView mUserInfoStatus;
    private CircleImageView mUserInfoImage;
    private Button mRequestFriendButt;
    private DatabaseReference mDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mEndDatabase;

    private FirebaseUser mCurrentUser;
    private String mCurrent_state;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mCurrent_state = "not_friend";

        mToolbar = (Toolbar) findViewById(R.id.users_info_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile"); //set up toolbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set up back button


        mUserInfoName = (TextView) findViewById(R.id.user_info_username);
        mUserInfoStatus = (TextView)findViewById(R.id.user_info_status);
        mUserInfoImage = (CircleImageView)findViewById(R.id.user_info_img);
        mRequestFriendButt = (Button)findViewById(R.id.request_friend_butt);

        final String user_uid = getIntent().getStringExtra("user_uid");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();//get current user
        final String current_user_uid = mCurrentUser.getUid();//get user uid

        mEndDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                //when add, update data or retrieve data

                mUserInfoName.setText(name);
                mUserInfoStatus.setText(status);

                if(!image.equals("default")) //if the image link is not "default" then load the image url
                {
                    Picasso.with(UserInfoActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mUserInfoImage);
                    //A powerful image downloading and caching library for Android
                }

                //------------friends feature(accept)-------------
                mFriendRequestDatabase.child(current_user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_uid))// if there is data under current user
                        {
                            String request_type = dataSnapshot.child(user_uid).child("request_type").getValue().toString();//check the data type
                            if(request_type.equals("received")) //if the the current recieve a request
                            {
                                mCurrent_state = "request_received";
                                mRequestFriendButt.setText("Accept Request"); //change the button text to accept
                            }

                            else if (request_type.equals("sent")) //if the current sent a request
                            {
                                mCurrent_state = "request_sent"; //change the current state
                                mRequestFriendButt.setText("Cancel Request");

                            }

                        }

                        else
                        {
                            mFriendDatabase.child(current_user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_uid))
                                    {
                                        mCurrent_state = "friends";
                                        mRequestFriendButt.setEnabled(false);
                                        mRequestFriendButt.setText("Already Friends");
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(UserInfoActivity.this, "Database Error: friends feature(accept)", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(UserInfoActivity.this, "Database Error: friends feature(accept)", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
            --------------------Requesting Friend----------------
         */

        mRequestFriendButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mRequestFriendButt.setEnabled(false);

                if (mCurrent_state.equals("not_friend"))
                {
                    mFriendRequestDatabase.child(current_user_uid).child(user_uid).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                mFriendRequestDatabase.child(user_uid).child(current_user_uid).child("request_type")
                                        .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mCurrent_state = "request_sent";
                                            mRequestFriendButt.setEnabled(true);
                                            mRequestFriendButt.setText("Cancel Request");
                                            Toast.makeText(UserInfoActivity.this, "Send and Receive Request successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UserInfoActivity.this, "Receive Request failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(UserInfoActivity.this, "Send Request failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
                /*
                    --------------------Cancel Friend Request----------------
                */

                if(mCurrent_state.equals("request_sent"))
                {
                    mFriendRequestDatabase.child(current_user_uid).child(user_uid)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                mFriendRequestDatabase.child(user_uid).child(current_user_uid)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            mCurrent_state = "not_friend";
                                            mRequestFriendButt.setEnabled(true);
                                            mRequestFriendButt.setText("Request as friend");
                                            Toast.makeText(UserInfoActivity.this, "Send and Receive Request successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            else
                            {
                                Toast.makeText(UserInfoActivity.this, "Cancel Request failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if(mCurrent_state.equals("request_received"))
                {
                    final String time = DateFormat.getDateTimeInstance().format(new Date()); //get the current time

                    Map friends = new HashMap();
                    friends.put("Friends/"+ current_user_uid+ "/" + user_uid + "/date" , time);
                    friends.put("Friends/"+ user_uid+ "/" + current_user_uid + "/date" , time);

                    friends.put("Friend_Request/"+ current_user_uid+ "/" + user_uid , null);
                    friends.put("Friend_Request/"+ user_uid+ "/" + current_user_uid, null);

                    mEndDatabase.updateChildren(friends, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)

                        {
                            if(databaseError == null)
                            {
                                mCurrent_state = "friends";
                                mRequestFriendButt.setEnabled(false);
                                mRequestFriendButt.setText("Already Friends");
                                Toast.makeText(UserInfoActivity.this, "Send and Receive Request successful", Toast.LENGTH_SHORT).show();
                            }

                            else
                            {
                                Toast.makeText(UserInfoActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
































                    /*final String time = DateFormat.getDateTimeInstance().format(new Date()); //get the current time
                    mFriendDatabase.child(current_user_uid).child(user_uid).setValue(time).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                mFriendDatabase.child(user_uid).child(current_user_uid).setValue(time).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful()) //if both set time successful, remove the friend requests
                                        {
                                            mFriendRequestDatabase.child(current_user_uid).child(user_uid)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful())
                                                    {
                                                        mFriendRequestDatabase.child(user_uid).child(current_user_uid)
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    mCurrent_state = "friends";
                                                                    mRequestFriendButt.setEnabled(false);
                                                                    mRequestFriendButt.setText("Already Friends");
                                                                    Toast.makeText(UserInfoActivity.this, "Send and Receive Request successful", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    else
                                                    {
                                                        Toast.makeText(UserInfoActivity.this, "Add friend failed", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                        }
                                    }
                                });
                            }

                            else
                            {
                                Toast.makeText(UserInfoActivity.this, "Add friend failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                   */
                }









        }
        });


    }
}
