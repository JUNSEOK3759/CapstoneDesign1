package com.work.cafe.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.work.cafe.data.CafePreviewData;
import com.work.cafe.data.UserData;

import java.util.ArrayList;
import java.util.Map;

public class UserModel {


    private static final String TAG = LoginModel.class.getSimpleName();

    private static final String BOOKMARK_URL = "bookmark";


    public static void addBookmark(UserData userData, String placeID, CafePreviewData cafePreviewData, DetailBookmarkCallback bookmarkCallback) {
        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(BOOKMARK_URL).child(userData.email).child(placeID).setValue(cafePreviewData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "create onSuccess");
                bookmarkCallback.onAddBookmark(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                bookmarkCallback.onAddBookmark(false);
            }
        });
    }

    public static void removeBookmark(UserData userData, String placeID, DetailBookmarkCallback bookmarkCallback) {
        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(BOOKMARK_URL).child(userData.email).child(placeID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "create onSuccess");
                bookmarkCallback.onRemoveBookmark(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                bookmarkCallback.onRemoveBookmark(false);
            }
        });
    }

    public static void getBookmark(UserData userData, String placeID, DetailBookmarkCallback bookmarkCallback) {

        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(BOOKMARK_URL).child(userData.email).child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bookmarkCallback.onGetBookmark(true, true);
                } else {
                    bookmarkCallback.onGetBookmark(true, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bookmarkCallback.onGetBookmark(false, false);
            }
        });

    }

    public static void getBookmarkList(UserData userData, BookmarkListCallback bookmarkListCallback) {

        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(BOOKMARK_URL).child(userData.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    ArrayList<CafePreviewData> cafePreviewDatas = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        cafePreviewDatas.add(0, dataSnapshot.getValue(CafePreviewData.class));
                    }

                    bookmarkListCallback.onResponse(true, cafePreviewDatas);
                } else {
                    bookmarkListCallback.onResponse(true, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bookmarkListCallback.onResponse(false, null);
            }
        });
    }


    public interface DetailBookmarkCallback {
        void onAddBookmark(boolean isSuccess);

        void onRemoveBookmark(boolean isSuccess);

        void onGetBookmark(boolean isSuccess, boolean isBookmark);
    }

    public interface BookmarkListCallback {
        void onResponse(boolean isSuccess, ArrayList<CafePreviewData> cafePreviewDataList);
    }

}
