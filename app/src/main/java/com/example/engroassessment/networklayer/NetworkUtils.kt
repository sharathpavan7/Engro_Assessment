package com.example.engroassessment.networklayer

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkUtils {

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
}