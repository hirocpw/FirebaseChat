package com.hiro.pchen.firebasechat.ChatModel;

import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiro.pchen.firebasechat.AccountModel.SettingsActivity;
import com.hiro.pchen.firebasechat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ABB89 on 2017/10/4.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mEndDatabase;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList)
    {
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_layout ,parent, false);

        return new MessageViewHolder(v);

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView message;
        public CircleImageView img;
        //public TextView displayName;

        public  MessageViewHolder(View view){
            super (view);

            message = (TextView) view.findViewById(R.id.talk_to_message);
            img = (CircleImageView) view.findViewById(R.id.talk_to_img);

        }

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {
        mEndDatabase = FirebaseDatabase.getInstance().getReference();
        Messages chat = mMessageList.get(i);
        String talking_user_id = chat.getFrom();

        mEndDatabase.child("Users").child(talking_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(viewHolder.img.getContext()).load(image).placeholder(R.drawable.default_avatar).into(viewHolder.img);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        viewHolder.message.setText(chat.getMessage());
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
