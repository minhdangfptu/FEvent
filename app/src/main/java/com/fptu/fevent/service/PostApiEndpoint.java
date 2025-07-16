package com.fptu.fevent.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PostApiEndpoint {

    // GET all posts
    @GET("posts")
    Call<List<Post>> getAllPosts();

    // GET single post by ID
    @GET("posts/{id}")
    Call<Post> getPostById(@Path("id") int id);

    // POST new post
    @POST("posts")
    Call<Post> createPost(@Body Post post);

    // PUT update post
    @PUT("posts/{id}")
    Call<Post> updatePost(@Path("id") int id, @Body Post post);

    // DELETE post
    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);
}