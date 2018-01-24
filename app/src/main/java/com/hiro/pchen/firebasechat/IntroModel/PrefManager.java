package com.hiro.pchen.firebasechat.IntroModel;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pchen on 2017/9/8.
 * Learned from Ravi Tamada, but has some modification by myself
 * Source: https://www.androidhive.info/2016/05/android-build-intro-slider-app/
 */

public class PrefManager {
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    Context mContext;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "welcome";

    private static final String First_Time = "FirstTimeYes";

    //Use SharedPreferences to store a boolean value indicating first time launch
    public PrefManager(Context context) {
        this.mContext = context;
        mPref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        mEditor.putBoolean(First_Time, isFirstTime);
        mEditor.commit();
    }

    public boolean isFirstTime() {
        return mPref.getBoolean(First_Time, true);
    }

}
