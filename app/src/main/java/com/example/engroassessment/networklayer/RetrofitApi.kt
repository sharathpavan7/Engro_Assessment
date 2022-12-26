package com.example.engroassessment.networklayer

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface RetrofitApi {

    @Multipart
    @POST("upload")
    fun uploadMultipleFiles(
        @Part surveyImage: Array<MultipartBody.Part?>,
        @Part("DRA") response: JsonObject
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET
    fun getNews(@Url response: String): Call<JsonObject>

}