package com.work.cafe.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private static FirebaseDatabase database;

    private FirebaseManager() {
    }

    public static FirebaseDatabase get() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

}
