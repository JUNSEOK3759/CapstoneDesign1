package com.work.cafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.work.cafe.data.CafeMarker;
import com.work.cafe.data.CafeMarkerData;
import com.work.cafe.data.Result;
import com.work.cafe.data.UserData;
import com.work.cafe.model.CafeModel;
import com.work.cafe.model.HTTPManager;
import com.work.cafe.model.PlaceAPI;
import com.work.cafe.model.UserSharedModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, Callback<CafeMarker>, GoogleMap.OnMarkerClickListener, CafeModel.CafeMarkerCallback {

    private final String TAG = MainActivity.class.getSimpleName();

    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap mMap;

    private Location location;

    private final String placeAPIKey = "AIzaSyAyviNF-w10cfli0zQnmBFeYHnAvGj7QoE";
    private final String placeAPIType = "cafe";
    private final String radius = "500";
    private String location_param = "0/0";

    private Handler mainHandler;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.cafe_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_cafe(mMap.getCameraPosition().target);
            }
        });


        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        mainHandler = new Handler();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                CafeDetailActivity.start(this, place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                assert status.getStatusMessage() != null;
                Log.i(TAG, status.getStatusMessage());
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmark_cafe: {

                UserData userData = UserSharedModel.getInstance(this).get();
                if (userData != null) {
                    Log.e(TAG, "onOptionsItemSelected: userData != null /// userData = " + userData);
                    BookmarkListActivity.start(this, userData);
                    return true;
                } else {
                    return false;
                }
            }

            case R.id.search_cafe: {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);


                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1000);
            return;
        }

        getLocation();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: permission failures");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1000);
        } else {
            getLocation();
        }
    }

    private void getLocation() {

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d(TAG, "onSuccess: " + location.getLatitude());
                            LatLng cafe_LatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            search_cafe(cafe_LatLng);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cafe_LatLng, 15f));


                        } else {
                            Log.d(TAG, "onSuccess: null");
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void search_cafe(LatLng cafe_LatLng) {
        findViewById(R.id.loading).setVisibility(View.VISIBLE);

        location_param = cafe_LatLng.latitude + "," + cafe_LatLng.longitude;

        CafeModel.getCafeMarks(location_param, radius, mainHandler, this);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(cafe_LatLng));
    }

    @Override
    public void onResponse(Call<CafeMarker> call, Response<CafeMarker> response) {
        CafeMarker cafeMarker = response.body();

        String pageToken = cafeMarker.getNextPageToken();

        Log.e(TAG, "onResponse: " + pageToken);
        Log.e(TAG, "onResponse: cafeMarker.getResults().size() = " + cafeMarker.getResults().size());
        if (cafeMarker.getNextPageToken() != null) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Call<CafeMarker> cafeCall =
                            HTTPManager.get().create(PlaceAPI.class).doGetListResources(
                                    placeAPIKey, location_param, radius, placeAPIType, pageToken);
                    cafeCall.enqueue(MainActivity.this);
                }
            }, 1300);
        } else {
            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
        }


        for (int i = 0; i < cafeMarker.getResults().size(); i++) {
            Result result = cafeMarker.getResults().get(i);
            com.work.cafe.data.legacy.Location cafe_location = result.getGeometry().getLocation();
            LatLng cafe_latlng = new LatLng(cafe_location.getLat(), cafe_location.getLng());

            MarkerOptions markerOptions = new MarkerOptions().position(cafe_latlng).title(result.getName());
            String placeID = result.getPlaceId();
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(placeID);
        }
    }

    @Override
    public void onFailure(Call<CafeMarker> call, Throwable t) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String placeID = marker.getTag().toString();
        CafeDetailActivity.start(this, placeID);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onMarkerData(boolean isSuccess, ArrayList<CafeMarkerData> cafeMarkerDataList) {
        findViewById(R.id.loading).setVisibility(View.INVISIBLE);
        float ratio;

            for (CafeMarkerData cafeMarkerData : cafeMarkerDataList) {

                LatLng cafe_latlng = cafeMarkerData.getCafeLatLng();

            MarkerOptions markerOptions = new MarkerOptions().position(cafe_latlng).title(cafeMarkerData.getCafeName());

            if (cafeMarkerData.cafeStatusData != null) {
                int status = cafeMarkerData.cafeStatusData.personnelStatus;
                int max = cafeMarkerData.cafeStatusData.personnelMax;
                markerOptions.icon(getMarkerIcon(((float) status / (float) max)));
                Log.d(TAG, "onMarkerData: ratio = " + (float) status / (float) max);
                markerOptions.icon(getMarkerIcon((float) status / (float) max));
            }

            String placeID = cafeMarkerData.getCafeId();
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(placeID);
        }


    }


    private BitmapDescriptor getMarkerIcon(float ratio) {


        Log.d(TAG, "getMarkerIcon: ratio = " + ratio);

        String color;


        if (ratio >= 0.8) {
            color = "#5F00FF";   //보라
        } else if (  ratio >= 0.5 && ratio < 0.8) {
            color = "#00ff00";   //초록
        } else{
            color = "#ffff00";   //노랑
        }

        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}