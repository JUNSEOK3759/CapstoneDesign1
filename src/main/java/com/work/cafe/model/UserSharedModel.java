package com.work.cafe.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.work.cafe.data.UserData;

public class UserSharedModel {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final String preferenceName = "user";
    private final String userKey = "user";

    private static UserSharedModel instance;

    public static UserSharedModel getInstance(Context context) {
        if (instance == null) {
            new UserSharedModel(context);
        }
        return instance;
    }

    public UserSharedModel (Context context) {

        preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        instance = this;
    }

    public UserData get() {
        String userDataString =  preferences.getString(userKey, null);
        if (userDataString == null) {
            return null;
        }
        return new Gson().fromJson(userDataString, UserData.class);
    }

    public void writeData (UserData userData) {
        editor.putString(userKey, new Gson().toJson(userData));
        editor.commit();
    }

    public void remove() {
        editor.remove(userKey);
        editor.commit();
    }
}
