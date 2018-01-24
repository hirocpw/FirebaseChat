package com.hiro.pchen.firebasechat.MainModel;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hiro.pchen.firebasechat.AccountModel.AllUserActivity;
import com.hiro.pchen.firebasechat.AccountModel.SettingsActivity;
import com.hiro.pchen.firebasechat.R;
import com.hiro.pchen.firebasechat.StartActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Sign Out";
    private FirebaseAuth mAuth;//Firebase
    private Toolbar mToolbar; //declare the tool bar variable
    private ViewPager mMainViewPager; // declare view pager
    private MainPagerAdapter mMainPagerAdapter;//declare pager adpater
    private TabLayout mTabs;//declare the options

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();//Firebase code
        mToolbar = (Toolbar) findViewById(R.id.tool_bar_line);//Set up the tool bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Firebase Chat");//Set up tool bar title

        mMainViewPager = (ViewPager)findViewById(R.id.main_view_pagers);
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mMainViewPager.setAdapter(mMainPagerAdapter);//set up adapter for view pager
        mTabs = (TabLayout)findViewById(R.id.main_tab);
        mTabs.setupWithViewPager(mMainViewPager);//set up tabs with the main view pager
        //create tabs request/friends/messages

    }


    @Override
    public void onStart(){
            super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //get the current user information
        if(currentUser == null){//if there is no user sign in at a moment
             BackToWelcomePage();//call BackToWelcomePage function
            //if the user is not signed in, return the user to the welcome page
        }
    }

    private void BackToWelcomePage(){
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
        //Sent the user back to the first welcome page
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);//get menu file
        return true;
        //create menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //when options in menu items are selected
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.sign_out_butt) { //if the sign out button is clicked
            FirebaseAuth.getInstance().signOut();//sign out
            Toast.makeText(MainActivity.this, "Sign Out Successful", Toast.LENGTH_SHORT).show(); //display successful message
            Log.d(TAG, "createUserWithEmail:success");
            BackToWelcomePage();//send user back to the first page, which is the welcome page
        }

        if(item.getItemId() == R.id.setting_butt){

            Intent setting_intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(setting_intent);
            //if user select setting button, lead them to the setting activity
        }

        if(item.getItemId() == R.id.all_user_butt){

            Intent all_user_intent = new Intent(MainActivity.this, AllUserActivity.class);
            startActivity(all_user_intent);
            //if user select all user button, lead them to the all user activity
        }


        return true;
    }
}
