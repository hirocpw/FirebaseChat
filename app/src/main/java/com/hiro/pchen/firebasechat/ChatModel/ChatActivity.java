package com.hiro.pchen.firebasechat.ChatModel;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.hiro.pchen.firebasechat.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatToUserID;
    private String mChatToUserName;
    private String mChatToUserImg;
    private Toolbar mToolbar;
    private DatabaseReference mEndDatabase;
    private String mCurrentUser;
    private EditText mTextBox;
    private ImageButton mTextSendButt;
    private CircleImageView mUserImg;
    private RecyclerView mConversationList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mMessageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToUserID = getIntent().getStringExtra("user_id");
        mChatToUserName = getIntent().getStringExtra("user_name");
        mChatToUserImg = getIntent().getStringExtra("thumb_img_link");
        mTextBox = (EditText)findViewById(R.id.enter_chat_box);
        mTextSendButt = (ImageButton) findViewById(R.id.enter_text_butt);

        mMessageAdapter = new MessageAdapter(messagesList);

        mConversationList = (RecyclerView) findViewById(R.id.conversation_list);
        mLinearLayout = new LinearLayoutManager(this);
        mConversationList.setHasFixedSize(true);
        mConversationList.setLayoutManager(mLinearLayout);
        mConversationList.setAdapter(mMessageAdapter);


        mToolbar = (Toolbar)findViewById(R.id.chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mChatToUserName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEndDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        
        loadMessages();


        mTextSendButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendText();

            }
        });


    }

    private void loadMessages()
    {
        mEndDatabase.child("Messages").child(mCurrentUser).child(mChatToUserID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                       Messages message = dataSnapshot.getValue(Messages.class);

                        messagesList.add(message);
                        mMessageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public void sendText()
    {
        String text = mTextBox.getText().toString();

        if (!TextUtils.isEmpty(text))
        {
            String current_user_ref = "Messages/"+ mCurrentUser +"/" +mChatToUserID;
            String talk_to_user_ref = "Messages/" +mChatToUserID+"/" +mCurrentUser;

            DatabaseReference message_push = mEndDatabase.child("Messages")
                    .child(mCurrentUser).child(mChatToUserID).push();

            String push_key = message_push.getKey();


            Map conversationMap = new HashMap();
            conversationMap.put( "message", text);
            conversationMap.put("from",mCurrentUser);
            conversationMap.put("time", ServerValue.TIMESTAMP);

            Map messageMap = new HashMap();
            messageMap.put(current_user_ref + "/" + push_key, conversationMap );
            messageMap.put(talk_to_user_ref +"/"+ push_key,conversationMap);

            mTextBox.setText(null);

            mEndDatabase.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {

                    if(databaseError != null)
                    {
                        Log.d("Chat_log",databaseError.getMessage().toString());
                    }


                }
            });




        }
    }





}
