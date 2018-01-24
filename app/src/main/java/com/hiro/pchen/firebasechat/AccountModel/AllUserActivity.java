package com.hiro.pchen.firebasechat.AccountModel;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.hiro.pchen.firebasechat.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mAllUserList;
    private DatabaseReference mFirebaseDatabase;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        mToolbar = (Toolbar)findViewById(R.id.all_user_tool);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users"); //set up toolbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set up back button


        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mAllUserList = (RecyclerView) findViewById(R.id.all_user_list);
        mAllUserList.setHasFixedSize(true);
        mAllUserList.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Users,AllUserHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, AllUserHolder>(
                Users.class,
                R.layout.user_list_layout,
                AllUserHolder.class,
                mFirebaseDatabase
        ) {
            @Override
            protected void populateViewHolder(AllUserHolder allUserViewHolder, Users users, int position) {

                allUserViewHolder.setName(users.getName());
                allUserViewHolder.setStatus(users.getStatus());
                allUserViewHolder.setThumbImage(users.getThumbImage(),getApplicationContext());

                final String user_uid = getRef(position).getKey();

                allUserViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent user_info_intent = new Intent(AllUserActivity.this, UserInfoActivity.class);
                        user_info_intent.putExtra("user_uid",user_uid);//sent user id to the userInfoActivity
                        startActivity(user_info_intent);

                    }
                });

            }
        };
        mAllUserList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class AllUserHolder extends RecyclerView.ViewHolder{

        View mView;

        public AllUserHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name)
        {
            TextView mUserNameView = (TextView) mView.findViewById(R.id.user_list_name);
            mUserNameView.setText(name);
        }

        public void setStatus(String status)
        {
            TextView mUserStatusView = (TextView) mView.findViewById(R.id.user_list_status);
            mUserStatusView.setText(status);
        }

        public void setThumbImage(String thumb_image, Context all_user_ac)
        {
            CircleImageView mUserImageView = (CircleImageView) mView.findViewById(R.id.user_list_image);
            Picasso.with(all_user_ac).load(thumb_image).placeholder(R.drawable.default_avatar).into(mUserImageView);
        }

    }
}
