package com.example.a10060.fakeface;

/**
 * Created by 10060 on 2018/5/22.
 */

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static com.android.volley.Request.Method.POST;

interface Service {
    @Multipart
    @POST("/demo/happy_android") //URL
    Call<ResponseBody> happy_image(@Part MultipartBody.Part image);

    @Multipart
    @POST("/demo/sad_android") //URL
    Call<ResponseBody> sad_image(@Part MultipartBody.Part image);
    @Multipart
    @POST("/demo/surprise_android") //URL
    Call<ResponseBody> surprise_image(@Part MultipartBody.Part image);
    @Multipart
    @POST("/demo/anger_android") //URL
    Call<ResponseBody> angry_image(@Part MultipartBody.Part image);

}