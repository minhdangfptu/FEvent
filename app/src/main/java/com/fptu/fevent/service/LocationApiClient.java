package com.fptu.fevent.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationApiClient {

    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";
    private static LocationApiClient instance;
    private LocationApiService locationApiService;

    private LocationApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        locationApiService = retrofit.create(LocationApiService.class);
    }

    public static LocationApiService getLocationApiService() {
        if (instance == null) {
            instance = new LocationApiClient();
        }
        return instance.locationApiService;
    }
}
