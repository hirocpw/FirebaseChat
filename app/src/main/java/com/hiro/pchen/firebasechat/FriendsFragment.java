package com.hiro.pchen.firebasechat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiro.pchen.firebasechat.AccountModel.AllUserActivity;
import com.hiro.pchen.firebasechat.AccountModel.Friends;
import com.hiro.pchen.firebasechat.AccountModel.UserInfoActivity;
import com.hiro.pchen.firebasechat.ChatModel.ChatActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUseDatabase;
    private FirebaseUser mCurrentUser;

    private RecyclerView mAllFriendList;
    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

       // mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_user_uid = mCurrentUser.getUid();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_uid);
        mUseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAllFriendList = (RecyclerView) mMainView.findViewById(R.id.all_friend_list);
        mAllFriendList.setHasFixedSize(true);
        mAllFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;

    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends, AllFriendHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, AllFriendHolder>
                (
                        Friends.class,
                        R.layout.user_list_layout,
                        AllFriendHolder.class,
                        mFriendDatabase
                ) {
            @Override
            protected void populateViewHolder(final AllFriendHolder allFriendHolder, Friends friends, int i)
            {
                allFriendHolder.setDate(friends.getDate());

                final String user_id_list = getRef(i).getKey();
                mUseDatabase.child(user_id_list).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("name").getValue().toString();
                        final String thumb_link = dataSnapshot.child("thumb_image").getValue().toString();

                        allFriendHolder.setName(name);
                        allFriendHolder.setThumbImage(thumb_link,getContext());

                        allFriendHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Check Profile","Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select:");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0)
                                        {
                                            Intent user_info_intent = new Intent(getContext(), UserInfoActivity.class);
                                            user_info_intent.putExtra("user_uid",user_id_list);//sent user id to the userInfoActivity
                                            startActivity(user_info_intent);
                                        }

                                        if(which == 1)
                                        {
                                            Intent chat_intent = new Intent(getContext(), ChatActivity.class);
                                            chat_intent.putExtra("user_id",user_id_list);
                                            chat_intent.putExtra("user_name",name);
                                            chat_intent.putExtra("thumb_img_link",thumb_link);
                                            startActivity(chat_intent);

                                        }


                                    }
                                });
                             builder.show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mAllFriendList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class AllFriendHolder extends RecyclerView.ViewHolder {

        View mView;

        public AllFriendHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date)
        {
            TextView userNameView = (TextView)mView.findViewById(R.id.user_list_status);
            userNameView.setText(date);
        }

        public void setName(String name)
        {
            TextView userName = (TextView) mView.findViewById(R.id.user_list_name);
            userName.setText(name);
        }

        public void setThumbImage (String thumb_link, Context all_user_ac)
        {
            CircleImageView mUserImageView = (CircleImageView) mView.findViewById(R.id.user_list_image);
            Picasso.with(all_user_ac).load(thumb_link).placeholder(R.drawable.default_avatar).into(mUserImageView);
        }

    }
}
