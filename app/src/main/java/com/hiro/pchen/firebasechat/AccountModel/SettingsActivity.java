package com.hiro.pchen.firebasechat.AccountModel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hiro.pchen.firebasechat.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    //database and storage
    private DatabaseReference mDatabase; //database reference
    private FirebaseUser mCurrentUser; //firebase code
    private static final int image_choose = 1; //Any integer
    private StorageReference mStorageRef;
    //layout
    private CircleImageView mAvatar;
    private TextView mUsername;
    private TextView mUserStatus;
    private Button mChangeStatus;
    private Button mChangeImage;

    private ProgressDialog mProgress;//progress



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mStorageRef = FirebaseStorage.getInstance().getReference();//point to firebase storage

        mAvatar = (CircleImageView) findViewById(R.id.settings_img);
        mUserStatus = (TextView) findViewById(R.id.settings_status);
        mUsername = (TextView) findViewById(R.id.settings_username);
        mChangeStatus = (Button)findViewById(R.id.settings_status_butt);
        mChangeImage = (Button) findViewById(R.id.settings_img_butt);

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_status_change = new Intent(SettingsActivity.this, StatusActivity.class);
                startActivity(go_to_status_change);
            }
            //led user to status activity when click the button
        });

        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent choose_image = new Intent();
                choose_image.setType("image/*");
                choose_image.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(choose_image,"Select Image"),image_choose);
            }
        });


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();//get current user
        String current_user_uid = mCurrentUser.getUid();//get user uid
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_uid);//get to the user database by current user uid

        mDatabase.addValueEventListener(new ValueEventListener() {
            //retrieve data from firebase database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                //when add, update data or retrieve data

                mUsername.setText(name);
                mUserStatus.setText(status);

                if(!image.equals("default")) //if the image link is not "default" then load the image url
                {
                    Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mAvatar);
                    //A powerful image downloading and caching library for Android
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { //for handling errors

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == image_choose && resultCode == RESULT_OK) //if you can get the image
        {
            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)//set a square ratio for image cropper
                    .start(this);
            //crop the image
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                mProgress = new ProgressDialog(SettingsActivity.this);//initialise the progress animation
                mProgress.setTitle("Uploading..");
                mProgress.setMessage("Please wait..");
                mProgress.setCanceledOnTouchOutside(false);//if user touch something else, the progress still show
                mProgress.show();

                Uri resultUri = result.getUri();// get image uri

                String current_user_uid = mCurrentUser.getUid();//get user uid

                File thumb_image_path = new File(resultUri.getPath());//get image path for making it to a thumb image

                Bitmap thumb_image = new Compressor(this)
                        .setMaxHeight(200) //compressing the size
                        .setMaxWidth(200)
                        .setQuality(75) //compressing the quality
                        .compressToBitmap(thumb_image_path);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filePath = mStorageRef.child("users_avatar").child(current_user_uid + ".jpg");
                // Create a child reference
                // imagesRef now points to "users_avatar" in firebase
                //imagae name is the user current uid + jpg
                final StorageReference thumb_file_path = mStorageRef.child("users_avatar").child("thumbs").child(current_user_uid +".jpg");



                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    // upload the cropped image
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful()) //if upload ok
                        {
                            @SuppressWarnings("VisibleForTests")
                            final String downloadLink = task.getResult().getDownloadUrl().toString(); //get the img's url
                            UploadTask uploadTask = thumb_file_path.putBytes(thumb_byte);//upload thumb image
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    @SuppressWarnings("VisibleForTests")
                                    String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){
                                        Map update_hashMap= new HashMap<>();
                                        update_hashMap.put("image",downloadLink);
                                        update_hashMap.put("thumb_image",thumb_download_url);
                                        /*
                                        update both images' url
                                         */

                                        mDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            /*set the img and the thumb_img url according to update_hashmap
                                            into database so that we can retrieve later on display*/
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    mProgress.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "successful", Toast.LENGTH_SHORT).show();
                                                }

                                                else
                                                {
                                                    Toast.makeText(SettingsActivity.this, "Img url database error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }

                                }
                            });



                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Upload Error", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }


    }
}
