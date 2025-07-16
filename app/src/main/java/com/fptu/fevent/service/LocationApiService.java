package com.fptu.fevent.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface LocationApiService {
    @GET("search")
    Call<List<LocationResult>> searchLocations(
            @Query("q") String query,
            @Query("format") String format,
            @Query("limit") int limit
    );
}
