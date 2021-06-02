package com.work.cafe.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.work.cafe.data.UserData;

public class LoginModel {


    private final String TAG = LoginModel.class.getSimpleName();


    private final String USER_URL = "user";

    public LoginModel() {
    }

    public void createUser(UserData userData, final SignUpCallback signUpCallback) {


        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(USER_URL).child(userData.getEmail()).setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "create onSuccess");
                signUpCallback.onCreateUser(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                signUpCallback.onCreateUser(false);
            }
        });
    }

    public void checkUserID(String email, final SignUpCallback loginCallback) {


        DatabaseReference databaseReference = FirebaseManager.get().getReference();


        databaseReference.child(USER_URL).child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loginCallback.onCheckEmail(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void loginUser(String email, final String pwd, final LoginCallback loginCallback) {

        DatabaseReference databaseReference = FirebaseManager.get().getReference();

        databaseReference.child(USER_URL).child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    loginCallback.onLogin(false, null);
                    return;
                }


                UserData userData = snapshot.getValue(UserData.class);
                loginCallback.onLogin(userData.pwd.equals(pwd), userData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public interface SignUpCallback {

        void onCheckEmail(boolean exits);

        void onCreateUser(boolean created);
    }

    public interface LoginCallback {
        void onLogin(boolean isLogin, UserData userData);
    }

}
