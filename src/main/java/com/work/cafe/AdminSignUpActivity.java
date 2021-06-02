package com.work.cafe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.work.cafe.data.UserData;
import com.work.cafe.model.LoginModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdminSignUpActivity extends AppCompatActivity
        implements
        View.OnClickListener, LoginModel.SignUpCallback, View.OnFocusChangeListener, OnMapReadyCallback {

    private final String TAG = AdminSignUpActivity.class.getName();

    private EditText id_view;
    private EditText pwd_view;
    private EditText pwd_check_view;
    private EditText nickname_view;

    private View email_check_light;
    private View pwd_check_light;

    private TextView email_warning;
    private TextView pwd_warning;

    private boolean email_check = false;
    private boolean pwd_check = false;

    private GoogleMap mMap;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    private Place cafePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "onCreate: mapFragment is null");
        }

        SearchView searchView = findViewById(R.id.cafe_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onClick: searchView.setOnClickListener");
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setInitialQuery(query)
                        .build(AdminSignUpActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        id_view = findViewById(R.id.edit_id);
        pwd_view = findViewById(R.id.edit_pwd);
        pwd_check_view = findViewById(R.id.edit_pwd_check);
        nickname_view = findViewById(R.id.edit_nickname);

        email_check_light = findViewById(R.id.edit_id_light);
        pwd_check_light = findViewById(R.id.edit_pwd_light);

        email_warning = findViewById(R.id.edit_id_warning);
        pwd_warning = findViewById(R.id.edit_pwd_check_warning);

        findViewById(R.id.sign_up).setOnClickListener(this);


        id_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                String id = id_view.getText().toString();

                if (!hasFocus && !id.isEmpty()) {
                    Log.d(TAG, "onFocusChange: email");

                    new LoginModel().checkUserID(id, AdminSignUpActivity.this);
                }
            }
        });

        pwd_view.setOnFocusChangeListener(this);
        pwd_check_view.setOnFocusChangeListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                SearchView searchView = findViewById(R.id.cafe_search_view);

                Place place = Autocomplete.getPlaceFromIntent(data);
                searchView.setQuery(place.getName(), false);

                cafePlace = place;
                mMap.clear();
                TextView cafeAddress = findViewById(R.id.cafe_address);
                cafeAddress.setText(place.getAddress());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15);
                mMap.moveCamera(cameraUpdate);
                MarkerOptions markerOptions = new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName());

                mMap.addMarker(markerOptions);
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
    public void onClick(View v) {

        if (v.getId() == R.id.sign_up) {
            if (email_check && pwd_check) {

                String id = id_view.getText().toString();
                String pwd = pwd_view.getText().toString();
                String nickname = nickname_view.getText().toString();
                UserData userData = new UserData(id, pwd, nickname, cafePlace.getName(), cafePlace.getId());
                new LoginModel().createUser(userData, this);
            } else {
                Toast.makeText(this, R.string.checking_sign_up_text, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCheckEmail(boolean exits) {

        Log.d(TAG, "onCheckEmail: exits = " + exits);
        email_check = !exits;
        if (exits) {
            email_check_light.setBackgroundColor(getResources().getColor(R.color.colorFailure));
            email_warning.setText(R.string.checking_email_warning);
        } else {
            email_check_light.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
            email_warning.setText("");
        }
    }

    @Override
    public void onCreateUser(boolean created) {

        if (created) {
            finish();
            Toast.makeText(this, R.string.sign_up_success, Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, R.string.sign_up_failure, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        String pwd = pwd_view.getText().toString();
        String pwd_check = pwd_check_view.getText().toString();

        if (!hasFocus && !pwd.isEmpty() && !pwd_check.isEmpty()) {
            this.pwd_check = pwd.equals(pwd_check);

            if (this.pwd_check) {
                pwd_check_light.setBackgroundColor(getResources().getColor(R.color.colorSuccess));
                pwd_warning.setText("");
            } else {
                pwd_check_light.setBackgroundColor(getResources().getColor(R.color.colorFailure));
                pwd_warning.setText(R.string.checking_pwd_warning);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }
}