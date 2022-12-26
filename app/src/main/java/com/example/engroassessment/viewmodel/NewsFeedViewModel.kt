package com.example.engroassessment.viewmodel

import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.engroassessment.repository.NewsRepository
import com.google.gson.JsonObject

class NewsFeedViewModel : ViewModel() {

    var newsRepository = NewsRepository()

    fun getNews() : LiveData<JsonObject>{
        return newsRepository.getNews()
    }

    fun uploadFiles(filesList: ArrayList<Uri>) : MutableLiveData<JsonObject> {
        return newsRepository.uploadFiles(filesList)
    }
}