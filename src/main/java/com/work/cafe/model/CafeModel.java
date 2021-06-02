package com.work.cafe.model;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.work.cafe.data.CafeMarker;
import com.work.cafe.data.CafeMarkerData;
import com.work.cafe.data.UserData;
import com.work.cafe.data.detail.CafeDetailData;
import com.work.cafe.data.detail.Location;
import com.work.cafe.data.detail.Result;
import com.work.cafe.data.detail.Review;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeModel {

    private static final String TAG = LoginModel.class.getSimpleName();


    private static final String REVIEW_URL = "review";
    private static final String BOOKMARK_URL = "bookmark";
    private static final String BOOKING_URL = "booking";


    private static final String CAFE_URL = "cafe";



    public static void writeCafe(UserData userData, CafeDetailData cafeDetailData, String placeID, final CafeCallback cafeCallback) {

        DatabaseReference databaseReference = FirebaseManager.get().getReference();
        databaseReference.child(CAFE_URL).child(placeID).setValue(cafeDetailData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "create onSuccess");

                cafeCallback.onWriteCafe(true, cafeDetailData);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                cafeCallback.onWriteCafe(false, null);
            }
        });
    }

    public static void getCafe(String placeID, CafeCallback cafeCallback) {

        DatabaseReference databaseReference = FirebaseManager.get().getReference();

        databaseReference.child(CAFE_URL).child(placeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {

                    getCafePlace(placeID, cafeCallback);
                    return;
                }


                CafeDetailData cafeDetailData = snapshot.getValue(CafeDetailData.class);
                cafeCallback.onGetCafe(true, cafeDetailData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                getCafePlace(placeID, cafeCallback);

            }
        });

    }

    private static void getCafePlace(String placeID, CafeCallback cafeCallback) {
        HTTPManager.get().create(PlaceAPI.class)
                .doGetDetailsResources(
                        HTTPManager.PLACE_API_KEY,
                        placeID,
                        "ko"
                ).enqueue(new Callback<CafeDetailData>() {
            @Override
            public void onResponse(Call<CafeDetailData> call, Response<CafeDetailData> response) {
                if (response.body() != null) {
                    cafeCallback.onGetCafe(true, response.body());
                } else {
                    cafeCallback.onGetCafe(false, null);
                }
            }

            @Override
            public void onFailure(Call<CafeDetailData> call, Throwable t) {
                cafeCallback.onGetCafe(false, null);
            }
        });
    }

    public static void getCafeMarks(String location_param, String radius, Handler mainHandler, CafeMarkerCallback cafeMarkerCallback) {

        final String placeAPIKey = HTTPManager.PLACE_API_KEY;
        final String placeAPIType = "cafe";

        MarkerCallback markerCallback = new MarkerCallback(mainHandler, placeAPIKey, placeAPIType, location_param, radius, cafeMarkerCallback);

        Call<CafeMarker> call = HTTPManager.get().create(PlaceAPI.class).doGetListResources(placeAPIKey, location_param, radius, placeAPIType);
        call.enqueue(markerCallback);


    }

    public static class MarkerCallback implements Callback<CafeMarker> {

        private Handler mainHandler;
        private String placeAPIKey;
        private String placeAPIType;
        private String location_param;
        private String radius;
        private CafeMarkerCallback cafeMarkerCallback;


        private HashMap<String, CafeMarkerData> cafeMarkerDataMap;

        public MarkerCallback(
                Handler mainHandler,
                String placeAPIKey,
                String placeAPIType,
                String location_param,
                String radius,
                CafeMarkerCallback cafeMarkerCallback) {
            this.mainHandler = mainHandler;
            this.placeAPIKey = placeAPIKey;
            this.placeAPIType = placeAPIType;
            this.location_param = location_param;
            this.radius = radius;
            this.cafeMarkerCallback = cafeMarkerCallback;

            this.cafeMarkerDataMap = new HashMap<>();
        }

        @Override
        public void onResponse(Call<CafeMarker> call, Response<CafeMarker> response) {
            CafeMarker cafeMarker = response.body();

            String pageToken = cafeMarker.getNextPageToken();

            Log.e(TAG, "onResponse: " + pageToken);
            Log.e(TAG, "onResponse: cafeMarker.getResults().size() = " + cafeMarker.getResults().size());

            for (com.work.cafe.data.Result result : cafeMarker.getResults()) {
                CafeMarkerData cafeMarkerData = new CafeMarkerData();

                cafeMarkerData.cafeName = result.getName();
                cafeMarkerData.cafeId = result.getPlaceId();

                com.work.cafe.data.legacy.Location location = result.getGeometry().getLocation();
                cafeMarkerData.cafeLatLng = new LatLng(location.getLat(), location.getLng());

                cafeMarkerDataMap.put(cafeMarkerData.cafeId, cafeMarkerData);
            }

            if (cafeMarker.getNextPageToken() != null) {
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Call<CafeMarker> cafeCall =
                                HTTPManager.get().create(PlaceAPI.class).doGetListResources(
                                        placeAPIKey, location_param, radius, placeAPIType, pageToken);

                        cafeCall.enqueue(MarkerCallback.this);
                    }
                }, 1000);
            } else {
                onFinish();
            }
        }

        @Override
        public void onFailure(Call<CafeMarker> call, Throwable t) {
        }

        private void onFinish() {
            DatabaseReference databaseReference = FirebaseManager.get().getReference();

            databaseReference.child(CAFE_URL).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CafeDetailData cafeDetailData = dataSnapshot.getValue(CafeDetailData.class);

                        CafeMarkerData cafeMarkerData = new CafeMarkerData();

                        Result result = cafeDetailData.getResult();

                        cafeMarkerData.cafeId = result.getPlaceId();

                        Location location = result.getGeometry().getLocation();
                        cafeMarkerData.cafeLatLng = new LatLng(location.getLat(), location.getLng());

                        cafeMarkerData.cafeName = result.getName();
                        cafeMarkerData.cafeStatusData = result.getCafeStatusData();

                        cafeMarkerDataMap.put(cafeMarkerData.cafeId, cafeMarkerData);
                    }

                    ArrayList<CafeMarkerData> cafeMarkerDataList = new ArrayList<>(cafeMarkerDataMap.values());
                    cafeMarkerCallback.onMarkerData(true, cafeMarkerDataList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public interface CafeCallback {
        void onWriteCafe(boolean isSuccess, CafeDetailData cafeDetailData);

        void onGetCafe(boolean isSuccess, CafeDetailData cafeDetailData);
    }

    public interface CafeMarkerCallback {
        void onMarkerData(boolean isSuccess, ArrayList<CafeMarkerData> cafeMarkerDataList);
    }


    public interface ReviewCallback {
        void onReview(boolean reviewed, Review review);
    }



    public interface BookingCallback {
        void onBooking(boolean isBooking);
    }

    public interface GetCafeCallback {
        void onGetCafe(boolean getCafe);
    }
}
