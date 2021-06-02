package com.work.cafe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.work.cafe.adapter.ReviewAdapter;
import com.work.cafe.adapter.SliderAdapter;
import com.work.cafe.data.BookingData;
import com.work.cafe.data.CafePreviewData;
import com.work.cafe.data.CafeStatusData;
import com.work.cafe.data.UserData;
import com.work.cafe.data.detail.CafeDetailData;
import com.work.cafe.data.detail.Result;
import com.work.cafe.data.detail.Review;
import com.work.cafe.model.BookingModel;
import com.work.cafe.model.CafeModel;
import com.work.cafe.model.HTTPManager;
import com.work.cafe.model.PlaceAPI;
import com.work.cafe.model.UserModel;
import com.work.cafe.model.UserSharedModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeDetailActivity extends AppCompatActivity implements
        CafeModel.CafeCallback, UserModel.DetailBookmarkCallback, BookingModel.UserBookingCallback {

    public static void start(AppCompatActivity activity, String placeID) {
        Intent detailIntent = new Intent(activity, CafeDetailActivity.class);
        detailIntent.putExtra(PLACE_ID_KEY, placeID);
        activity.startActivity(detailIntent);
    }

    private static final String PLACE_ID_KEY = "place_id";

    private static final String TAG = CafeDetailActivity.class.getName();

    private SliderAdapter sliderAdapter;
    private ReviewAdapter reviewAdapter;

    private RecyclerView reviewListView;
    private SliderView sliderView;

    private ImageButton bookmarkBtn;

    private CafeDetailData currentCafeDetailData;

    private String placeID;

    private boolean isBookmark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_detail);

        placeID = getIntent().getStringExtra(PLACE_ID_KEY);

        CafeModel.getCafe(placeID, this);


        sliderAdapter = new SliderAdapter(this);
        reviewAdapter = new ReviewAdapter();

        reviewListView = findViewById(R.id.review_list);
        reviewListView.setLayoutManager(new LinearLayoutManager(this));

        sliderView = findViewById(R.id.cafe_image_list);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();

        if (UserSharedModel.getInstance(this).get() == null) {
            findViewById(R.id.edit_review).setVisibility(View.GONE);
            findViewById(R.id.send_review).setVisibility(View.GONE);
            findViewById(R.id.review_rating).setVisibility(View.GONE);
            findViewById(R.id.booking_cafe).setVisibility(View.GONE);
        } else {
            UserData userData = UserSharedModel.getInstance(CafeDetailActivity.this).get();
            UserModel.getBookmark(userData, placeID, this);

            findViewById(R.id.edit_review).setVisibility(View.VISIBLE);
            findViewById(R.id.send_review).setVisibility(View.VISIBLE);
            findViewById(R.id.review_rating).setVisibility(View.VISIBLE);
            findViewById(R.id.booking_cafe).setVisibility(View.VISIBLE);

            findViewById(R.id.booking_cafe).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookingData bookingData = new BookingData();
                    bookingData.cafeID = placeID;
                    bookingData.nickname = userData.nickname;
                    bookingData.personnel = 5;
                    bookingData.scheduled = 5;
                    bookingData.userEmail = userData.email;

                    BookingActivity.start(CafeDetailActivity.this, bookingData);
                }
            });

            findViewById(R.id.send_review).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserData userData = UserSharedModel.getInstance(CafeDetailActivity.this).get();
                    RatingBar ratingBar = findViewById(R.id.review_rating);
                    EditText editText = findViewById(R.id.edit_review);

                    Review review = new Review();
                    review.setAuthorName(userData.nickname);
                    review.setRating((int) ratingBar.getRating());
                    review.setText(editText.getText().toString());


                    currentCafeDetailData.getResult().getReviews().add(0, review);
                    CafeModel.writeCafe(userData, currentCafeDetailData, placeID, CafeDetailActivity.this);
                }
            });

            bookmarkBtn = findViewById(R.id.bookmark_cafe);
            bookmarkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: bookmark_cafes");

                    if (currentCafeDetailData == null) {
                        return;
                    }

                    if (isBookmark) {
                        UserModel.removeBookmark(userData, placeID, CafeDetailActivity.this);

                    } else {
                        Result result = currentCafeDetailData.getResult();

                        CafePreviewData cafePreviewData = new CafePreviewData();
                        cafePreviewData.setAdrAddress(result.getAdrAddress());
                        cafePreviewData.setFormattedAddress(result.getFormattedAddress());
                        cafePreviewData.setName(result.getName());
                        cafePreviewData.setUserRatingsTotal(result.getUserRatingsTotal());
                        cafePreviewData.setPlaceId(result.getPlaceId());

                        if (currentCafeDetailData.getResult().getPhotos().size() > 0) {
                            cafePreviewData.setPhoto(currentCafeDetailData.getResult().getPhotos().get(0));
                        }

                        UserModel.addBookmark(userData, placeID, cafePreviewData, CafeDetailActivity.this);

                    }

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (BookingActivity.handlingResult(requestCode, resultCode, data)) {
            Log.e(TAG, "onActivityResult: accept");
            Toast.makeText(this, "예약이 완료 되었습니다.", Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "onActivityResult: reject");
            Toast.makeText(this, "예약이 거부 되었습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void init(CafeDetailData cafeDetailData) {
        currentCafeDetailData = cafeDetailData;
        Result result = cafeDetailData.getResult();

        TextView cafeName = findViewById(R.id.cafe_name);


        TextView cafeConsumerStatus = findViewById(R.id.cafe_consumer_status);

        cafeName.setText(result.getName());

        if (result.getPhotos() != null) {
            sliderAdapter.renewItems(new ArrayList<>(result.getPhotos()));
            sliderView.setSliderAdapter(sliderAdapter);
        }

        if (result.getReviews() != null) {
            reviewAdapter.setReviews(new ArrayList<>(result.getReviews()));
            reviewListView.setAdapter(reviewAdapter);
        }

        if (result.getCafeStatusData() != null) {
            CafeStatusData cafeStatusData = result.getCafeStatusData();
            String statusString = "현재 인원 " + cafeStatusData.personnelStatus + " / " + "최대 인원" + cafeStatusData.personnelMax;

            TextView statusView = findViewById(R.id.cafe_consumer_status);
            statusView.setText(statusString);
        }


        if (result.getOpeningHours() != null) {
            TextView cafeTime = findViewById(R.id.cafe_time);

            List<String> cafeTimeList = result.getOpeningHours().getWeekdayText();
            StringBuilder cafeTimeStrings = new StringBuilder();
            for (int i = 0; i < cafeTimeList.size(); i++) {
                cafeTimeStrings.append(cafeTimeList.get(i));
                if (i + 1 == cafeTimeList.size()) {
                    break;
                }
                cafeTimeStrings.append("\n");
            }
            cafeTime.setText(cafeTimeStrings.toString());
        }
    }

    private void setBookmarkView(boolean isBookmark) {
        if (isBookmark) {
            bookmarkBtn.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            bookmarkBtn.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    @Override
    public void onWriteCafe(boolean isSuccess, CafeDetailData cafeDetailData) {
        if (isSuccess) {
            init(cafeDetailData);
        } else {
            Log.e(TAG, "onWriteCafe: error! " + cafeDetailData);
        }
    }

    @Override
    public void onGetCafe(boolean isSuccess, CafeDetailData cafeDetailData) {
        if (isSuccess) {
            init(cafeDetailData);
        } else {

            Log.e(TAG, "onGetCafe: cafeDetilData = " + cafeDetailData);
        }
    }

    @Override
    public void onAddBookmark(boolean isSuccess) {
        Log.d(TAG, "onAddBookmark: isSuccess = " + isSuccess);
        isBookmark = true;
        setBookmarkView(isBookmark);
    }

    @Override
    public void onRemoveBookmark(boolean isSuccess) {
        Log.e(TAG, "onRemoveBookmark: isSuccess = " + isSuccess);
        isBookmark = false;
        setBookmarkView(isBookmark);
    }

    @Override
    public void onGetBookmark(boolean isSuccess, boolean isBookmark) {
        if (isSuccess && isBookmark) {
            this.isBookmark = true;
        } else {
            this.isBookmark = false;
        }
        setBookmarkView(this.isBookmark);
    }


    @Override
    public void onResponse(boolean isAccept, BookingData bookingData) {
        Log.d(TAG, "onResponse: isAccept = " + isAccept);
        Log.d(TAG, "onResponse: bookingData = " + bookingData);
    }
}