package com.hiro.pchen.firebasechat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.hiro.pchen.firebasechat.AccountModel.Friends;
import com.hiro.pchen.firebasechat.AccountModel.Requests;
import com.hiro.pchen.firebasechat.AccountModel.UserInfoActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendRequest;
    private DatabaseReference mUseDatabase;
    private FirebaseUser mCurrentUser;

    private RecyclerView mAllRequestList;
    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        // mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_user_uid = mCurrentUser.getUid();
        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(current_user_uid);
        mUseDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAllRequestList = (RecyclerView) mMainView.findViewById(R.id.all_request_list);
        mAllRequestList.setHasFixedSize(true);
        mAllRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestsFragment.AllRequestHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, AllRequestHolder>
                (
                        Requests.class,
                        R.layout.user_list_layout,
                        RequestsFragment.AllRequestHolder.class,
                        mFriendRequest
                ) {
            @Override
            protected void populateViewHolder(final RequestsFragment.AllRequestHolder allRequestHolder, Requests requests, int i)
            {

                final String user_id_list = getRef(i).getKey();
                mUseDatabase.child(user_id_list).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String thumb_link = dataSnapshot.child("thumb_image").getValue().toString();

                        allRequestHolder.setName(name);
                        allRequestHolder.setStatus(status);
                        allRequestHolder.setThumbImage(thumb_link,getContext());

                        allRequestHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{"Check Profile"};
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
        mAllRequestList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class AllRequestHolder extends RecyclerView.ViewHolder {

        View mView;

        public AllRequestHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setStatus(String status)
        {
            TextView mUserStatusView = (TextView) mView.findViewById(R.id.user_list_status);
            mUserStatusView.setText(status);
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
