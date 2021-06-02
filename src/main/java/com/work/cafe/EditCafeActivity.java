package com.work.cafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.github.loadingview.LoadingDialog;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.work.cafe.adapter.BookingAdapter;
import com.work.cafe.adapter.EditSliderAdapter;
import com.work.cafe.adapter.SliderAdapter;
import com.work.cafe.data.BookingData;
import com.work.cafe.data.CafeStatusData;
import com.work.cafe.data.UserData;
import com.work.cafe.data.detail.CafeDetailData;
import com.work.cafe.data.detail.Photo;
import com.work.cafe.data.detail.Result;
import com.work.cafe.model.BookingModel;
import com.work.cafe.model.CafeModel;
import com.work.cafe.model.ImageModel;
import com.work.cafe.model.UserSharedModel;

import java.util.ArrayList;
import java.util.List;

public class EditCafeActivity extends AppCompatActivity implements BookingAdapter.OnClickBooking, CafeModel.CafeCallback, BookingModel.CafeBookingCallback, EditSliderAdapter.OnClickRemove {

    private EditText statusView;
    private EditText maxView;

    private BookingAdapter bookingAdapter;

    private EditSliderAdapter sliderAdapter;

    private static final String TAG = EditCafeActivity.class.getName();

    private CafeDetailData cafeDetailData;

    private LoadingDialog loadingDialog;

    private RecyclerView cafeListView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cafe);

        sliderAdapter = new EditSliderAdapter(this, this);
        bookingAdapter = new BookingAdapter(this);

        UserData userData = UserSharedModel.getInstance(this).get();
        CafeModel.getCafe(userData.cafe_id, this);

        BookingModel.listenBooking(userData.cafe_id, this);

        cafeListView = findViewById(R.id.booking_list);

        linearLayoutManager = new LinearLayoutManager(this);

        cafeListView.setLayoutManager(linearLayoutManager);
        cafeListView.setAdapter(bookingAdapter);

        SliderView sliderView = findViewById(R.id.cafe_img);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.setSliderAdapter(sliderAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.image_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(EditCafeActivity.this)
                        .folderMode(true)
                        .toolbarFolderTitle("Folder")
                        .toolbarImageTitle("Tap to select")
                        .toolbarArrowColor(Color.BLACK)
                        .multi()
                        .limit(10)
                        .start();
            }
        });

        loadingDialog = LoadingDialog.Companion.get(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            ArrayList<Photo> photos = ImageModel.uploadImage(new ArrayList<>(images));

            for (Photo photo : photos) {
                cafeDetailData.getResult().getPhotos().add(0, photo);
            }

            sliderAdapter.renewItems(new ArrayList<>(cafeDetailData.getResult().getPhotos()));
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        updateCafeData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
}

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Log.d(TAG, "onOptionsItemSelected: item.getItemId() == R.id.action_edit");
            loadingDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UserData userData = UserSharedModel.getInstance(EditCafeActivity.this).get();
                    CafeModel.writeCafe(userData, cafeDetailData, userData.cafe_id, EditCafeActivity.this);
                }
            }, 2000);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickReject(BookingData bookingData, int position) {
        bookingData.isAccept = false;
        Toast.makeText(this, "예약을 거절했습니다.", Toast.LENGTH_LONG).show();
        BookingModel.responseBooking(bookingData);
    }

    @Override
    public void onClickAccept(BookingData bookingData, int position) {
        bookingData.isAccept = true;
        int status = cafeDetailData.getResult().getCafeStatusData().personnelStatus + bookingData.personnel;
        cafeDetailData.getResult().getCafeStatusData().personnelStatus = status;
        Toast.makeText(this, "예약을 승인하였습니다.", Toast.LENGTH_LONG).show();
        BookingModel.responseBooking(bookingData);
    }

    @Override
    public void onWriteCafe(boolean isSuccess, CafeDetailData cafeDetailData) {
        loadingDialog.hide();
        Toast.makeText(this, "저장 되었습니다.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetCafe(boolean isSuccess, CafeDetailData cafeDetailData) {
        if (isSuccess) {
            init(cafeDetailData);
        }
    }

    private void init(CafeDetailData cafeDetailData) {
        this.cafeDetailData = cafeDetailData;

        Result result = cafeDetailData.getResult();

        if (result.getPhotos() != null && result.getPhotos().size() > 0) {
            sliderAdapter.renewItems(new ArrayList<>(result.getPhotos()));
        }

        if (result.getCafeStatusData() != null) {
            CafeStatusData cafeStatusData = result.getCafeStatusData();
            statusView = findViewById(R.id.cafe_consumer_status);
            maxView = findViewById(R.id.cafe_consumer_max);

            statusView.setText(Integer.toString(cafeStatusData.personnelStatus));
            maxView.setText(Integer.toString(cafeStatusData.personnelMax));
        }

        TextView nameView = findViewById(R.id.cafe_name);
        nameView.setText(result.getName());
    }

    private void updateCafeData() {
        EditText statusView = findViewById(R.id.cafe_consumer_status);
        EditText maxView = findViewById(R.id.cafe_consumer_max);

        int max = Integer.parseInt(maxView.getText().toString());
        int status = Integer.parseInt(statusView.getText().toString());

        CafeStatusData cafeStatusData = new CafeStatusData();

        cafeStatusData.personnelStatus = status;
        cafeStatusData.personnelMax = max;

        cafeDetailData.getResult().setCafeStatusData(cafeStatusData);

        UserData userData = UserSharedModel.getInstance(this).get();
        CafeModel.writeCafe(userData, cafeDetailData, userData.cafe_id, this);
    }

    @Override
    public void onCanceled(BookingData bookingData) {
            bookingAdapter.removeBookingData(bookingData);
        }

        @Override
        public void onRequest(BookingData bookingData) {
            bookingAdapter.addBookingData(bookingData);
            Log.d(TAG, "onRequest: bookingData " + bookingData.cafeID);
            linearLayoutManager.scrollToPosition(0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, GlobalApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon)
                .setContentTitle("예약을 요청 하였습니다.")
                .setContentText(bookingData.nickname + " 님이 예약을 요청 하였습니다.")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onClick(Photo photo, int position) {
        sliderAdapter.deleteItem(position);
        cafeDetailData.getResult().getPhotos().remove(position);
    }
}