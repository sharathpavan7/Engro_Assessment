package com.example.engroassessment.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.engroassessment.networklayer.NetworkUtils
import com.example.engroassessment.networklayer.RetrofitApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NewsRepository {

    fun getNews(): MutableLiveData<JsonObject>{
        val data = MutableLiveData<JsonObject>()
        val connTime = 20L

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()
        val serviceUrl = "https://newsapi.org/v2/"
        val url = serviceUrl+ "top-headlines?country=us&category=business&apiKey=cc70841b70374ff7baae461fc11ea849"
        val retrofit = NetworkUtils().retrofitClient(connTime, serviceUrl, gson)
        val github = retrofit?.create(RetrofitApi::class.java)
        val call = github?.getNews(url)
        call?.enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    data.value = response.body()
                }else{
                    data.value = null
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
               data.value = null
            }

        })
        return data
    }

    fun uploadFiles(filesList: ArrayList<Uri>): MutableLiveData<JsonObject> {
        var data = MutableLiveData<JsonObject>()
        val connTimeout = 100L
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()
        val serviceBaseUrl = "https://upload.com/"

        val retrofit = NetworkUtils().retrofitClient(connTimeout, serviceBaseUrl, gson)
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
