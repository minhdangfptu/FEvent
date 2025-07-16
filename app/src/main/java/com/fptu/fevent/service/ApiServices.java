package com.fptu.fevent.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServices {

    private static final String BASE_URL = ApiConstants.POSTS_BASE_URL;

    private static ApiServices instance;

    private PostApiEndpoint postApiEndpoint;

    private ApiServices() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        postApiEndpoint = retrofit.create(PostApiEndpoint.class);
    }

    private static ApiServices getInstance() {
        if (instance == null) {
            instance = new ApiServices();
        }
        return instance;
    }

    public static PostApiEndpoint getPostApiEndpoint() {
        return getInstance().postApiEndpoint;
    }
}
