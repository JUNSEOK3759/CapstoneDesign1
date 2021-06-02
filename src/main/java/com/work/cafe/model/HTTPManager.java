package com.work.cafe.model;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HTTPManager {


    private final String PLACE_API_BASE_URL = "https://maps.googleapis.com/";
    public static final String PLACE_API_KEY = "AIzaSyAyviNF-w10cfli0zQnmBFeYHnAvGj7QoE";


    private static Retrofit retrofit;

    public HTTPManager() {

        OkHttpClient client = new OkHttpClient.Builder().build();


        retrofit = new Retrofit.Builder().baseUrl(PLACE_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


    public static Retrofit get() {
        if (retrofit == null) {
            new HTTPManager();
        }
        return retrofit;
    }

}
