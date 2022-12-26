package com.example.engroassessment.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.engroassessment.networklayer.RetrofitApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


class EngroFileUploadRepository {

    fun retrofitClient(connTimeout: Long, serviceBaseUrl: String?, gson: Gson?): Retrofit? {
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(connTimeout, TimeUnit.SECONDS)
            .readTimeout(connTimeout, TimeUnit.SECONDS).build()
        return Retrofit.Builder()
            .baseUrl(serviceBaseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun uploadFiles(filesList: ArrayList<Uri>): MutableLiveData<JsonObject> {
        var data = MutableLiveData<JsonObject>()
        val connTimeout = 100L
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()
        val serviceBaseUrl = ""

        val retrofit = retrofitClient(connTimeout, serviceBaseUrl, gson)
        val github = retrofit?.create(RetrofitApi::class.java)

        val engroImagesParts = arrayOfNulls<MultipartBody.Part>(filesList.size)
        for (index in filesList.indices) {
            val file = File(filesList[index].path)
            val surveyBody = RequestBody.create(
                "image/*".toMediaTypeOrNull(),
                file
            )
            engroImagesParts[index] = MultipartBody.Part.createFormData("Engro_Image",
                file.getName(),
                surveyBody);
        }

        val jsonObject = JsonObject()
        jsonObject.addProperty("userId", "12345")

        val call = github!!.uploadMultipleFiles(engroImagesParts, jsonObject)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    data.value = response.body()
                } else {
                    data.value = null
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                data.value = null
            }
        })

        return data
    }
}