package com.iyaselerehoboth.otrek.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context mCtx;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "OTrek Preferences";

    public static final String KEY_USER_FULL_NAME = "user_full_name";
    public static final String KEY_USER_EMAIL = "user_email_address";
    public static final String KEY_USER_PICTURE_LINK = "user_picture_url";

    public SessionManager(Context context){
        this.mCtx = context;
        prefs = mCtx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void setUserDetails(String userEmail, String userName, String userImage){
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putString(KEY_USER_FULL_NAME, userName);
        editor.putString(KEY_USER_PICTURE_LINK, userImage);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> map = new HashMap<>();

        map.put(KEY_USER_EMAIL, prefs.getString(KEY_USER_EMAIL, ""));
        map.put(KEY_USER_FULL_NAME, prefs.getString(KEY_USER_FULL_NAME, "User"));
        map.put(KEY_USER_PICTURE_LINK, prefs.getString(KEY_USER_PICTURE_LINK, ""));

        return map;
    }

    public void clearUserDetails(){
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_PICTURE_LINK);
        editor.remove(KEY_USER_PICTURE_LINK);
        editor.commit();
    }
}
