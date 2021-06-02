package com.work.cafe.model;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.work.cafe.data.BookingData;
import com.work.cafe.data.UserData;

public class BookingModel {

    private static final String TAG = BookingModel.class.getName();
    private static final String BOOKING_URL = "booking";

    public static void requestBooking(UserData userData, String placeID, BookingData bookingData, UserBookingCallback bookingCallback) {

        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        final BookingData[] serverBookingData = {null};

        databaseReference.child(BOOKING_URL).child(placeID).child(userData.email).setValue(bookingData);

        databaseReference.child(BOOKING_URL).child(placeID).child(userData.email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (serverBookingData[0] == null) {
                    BookingData bookingData = snapshot.getValue(BookingData.class);
                    serverBookingData[0] = bookingData;
                    return;
                }

                Log.d(TAG, "onDataChange: snapshot.exists() = " + snapshot.exists());
                BookingData bookingData = snapshot.getValue(BookingData.class);

                if (snapshot.exists()) {
                    Log.d(TAG, "onDataChange:  bookingData.isAccept " + bookingData.isAccept);

                    if (bookingData.isAccept) {
                        bookingCallback.onResponse(bookingData.isAccept, bookingData);
                        databaseReference.child(BOOKING_URL).child(placeID).child(userData.email).removeEventListener(this);
                    }

                } else {
                    bookingCallback.onResponse(false, bookingData);
                    databaseReference.child(BOOKING_URL).child(placeID).child(userData.email).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                databaseReference.child(BOOKING_URL).child(placeID).child(userData.email).removeEventListener(this);
            }
        });
    }

    public static void listenBooking(String placeID, CafeBookingCallback cafeBookingCallback) {
        Log.d(TAG, "listenBooking: placeID = " + placeID);
        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(BOOKING_URL).child(placeID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: snapshot.getKey() = " + snapshot.getKey());
                Log.d(TAG, "onChildAdded: previousChildName = " + previousChildName);
                BookingData bookingData = snapshot.getValue(BookingData.class);

                cafeBookingCallback.onRequest(bookingData);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                BookingData bookingData = snapshot.getValue(BookingData.class);
                cafeBookingCallback.onCanceled(bookingData);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void responseBooking(BookingData bookingData) {
        DatabaseReference databaseReference = FirebaseManager.get().getReference();

        if (bookingData.isAccept) {
            databaseReference.child(BOOKING_URL).child(bookingData.cafeID).child(bookingData.userEmail).setValue(bookingData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else {
            databaseReference.child(BOOKING_URL).child(bookingData.cafeID).child(bookingData.userEmail).removeValue();
        }

    }

    public interface UserBookingCallback {
        void onResponse(boolean isAccept, BookingData bookingData);
    }


    public interface CafeBookingCallback {
        void onCanceled(BookingData bookingData);

        void onRequest(BookingData bookingData);
    }


}
