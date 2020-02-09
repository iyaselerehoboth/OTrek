package com.iyaselerehoboth.otrek.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context mCtx;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "OTrek Preferences";

    public static final String KEY_USER_FULL_NAME = "full_name";
    public static final String KEY_USER_PICTURE_LINK = "profile_picture_link";

    public SessionManager(Context context){
        this.mCtx = context;
        prefs = mCtx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void setUserDetails(String fullName, String pictureLink){
        editor.putString(KEY_USER_FULL_NAME, fullName);
        editor.putString(KEY_USER_PICTURE_LINK, pictureLink);
        editor.commit();
    }
}
