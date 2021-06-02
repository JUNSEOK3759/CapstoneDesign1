package com.work.cafe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.work.cafe.data.BookingData;
import com.work.cafe.data.UserData;
import com.work.cafe.model.BookingModel;
import com.work.cafe.model.UserSharedModel;

public class BookingActivity extends AppCompatActivity implements BookingModel.UserBookingCallback {

    public static final int BOOKING_CODE = 1231;
    private static final String BOOKING_DATA_KEY = "booking_data";
    private static final String BOOKING_ACCEPT_KEY = "booking_accept";

    public static void start(AppCompatActivity activity, BookingData bookingData) {
        Intent startIntent = new Intent(activity, BookingActivity.class);
        startIntent.putExtra(BOOKING_DATA_KEY, bookingData);
        activity.startActivityForResult(startIntent, BOOKING_CODE);
    }

    private BookingData bookingData;

    private ViewGroup loading;
    private ViewGroup input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        findViewById(R.id.booking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView personnel = findViewById(R.id.personnel_input);
                TextView scheduled = findViewById(R.id.schedule_input);

                if (personnel.getText().toString().length() == 0 || scheduled.getText().toString().length() == 0) {
                    Toast.makeText(BookingActivity.this, "빈 칸 없이 입력 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                hideKeyboard(BookingActivity.this);

                bookingData.personnel = Integer.parseInt(personnel.getText().toString());
                bookingData.scheduled = Integer.parseInt(scheduled.getText().toString());

                UserData userData = UserSharedModel.getInstance(BookingActivity.this).get();

                BookingModel.requestBooking(userData, bookingData.cafeID, bookingData, BookingActivity.this);

                loading.setVisibility(View.VISIBLE);
                input.setVisibility(View.GONE);
            }
        });

        loading = findViewById(R.id.loading_booking);
        input = findViewById(R.id.input_booking);

    bookingData = (BookingData) getIntent().getSerializableExtra(BOOKING_DATA_KEY);
}

    @Override
    public void onResponse(boolean isAccept, BookingData bookingData) {
        getIntent().putExtra(BOOKING_ACCEPT_KEY, isAccept);
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

    public static boolean handlingResult(int requestCode, int resultCode, Intent data) {
        return requestCode == BOOKING_CODE && resultCode == RESULT_OK && data.getBooleanExtra(BOOKING_ACCEPT_KEY, false);
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}