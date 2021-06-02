package com.work.cafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.work.cafe.adapter.CafeListAdapter;
import com.work.cafe.data.CafePreviewData;
import com.work.cafe.data.UserData;
import com.work.cafe.model.UserModel;

import java.io.Serializable;
import java.util.ArrayList;

public class BookmarkListActivity extends AppCompatActivity implements UserModel.BookmarkListCallback, CafeListAdapter.OnCafeClickListener {

    private static final String CAFE_ID_KEY = "cafe_id";
    private static final String USER_DATA_KEY = "user_data";


    public static void start(Context context, UserData userData) {



        Intent startIntent = new Intent(context, BookmarkListActivity.class);

        startIntent.putExtra(USER_DATA_KEY, (Serializable) userData);

        context.startActivity(startIntent);
    }

    private CafeListAdapter cafeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_list);

        UserData userData = (UserData) getIntent().getSerializableExtra(USER_DATA_KEY);

        RecyclerView recyclerView = findViewById(R.id.cafe_list);

        cafeListAdapter = new CafeListAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cafeListAdapter);

        UserModel.getBookmarkList(userData, this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("즐겨찾기 목록");
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(boolean isSuccess, ArrayList<CafePreviewData> cafePreviewDataList) {
        if (isSuccess) {

            if (cafePreviewDataList != null) {
                cafeListAdapter.setCafePreviewDataList(cafePreviewDataList);
            }
        }
    }

    @Override
    public void onClick(CafePreviewData cafePreviewData, int position) {
        CafeDetailActivity.start(this, cafePreviewData.getPlaceId());
    }
}