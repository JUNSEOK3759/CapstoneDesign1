package com.work.cafe.model;

import com.work.cafe.data.CafeMarker;
import com.work.cafe.data.detail.CafeDetailData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceAPI {

    @GET("maps/api/place/nearbysearch/json")
    Call<CafeMarker> doGetListResources(
            @Query("key") String key,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type
    );

    @GET("maps/api/place/nearbysearch/json")
    Call<CafeMarker> doGetListResources(
            @Query("key") String key,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("pagetoken") String pageToken
    );

    @GET("maps/api/place/details/json")
    Call<CafeDetailData> doGetDetailsResources(
            @Query("key") String key,
            @Query("place_id") String placeid,
            @Query("language") String language
    );

}
