package com.example.engroassessment.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.engroassessment.repository.EngroFileUploadRepository
import com.google.gson.JsonObject

class EngroFileUploadViewmodel: ViewModel() {

    val engroFileUploadRepository = EngroFileUploadRepository()
    fun uploadFiles(filesList: ArrayList<Uri>) : MutableLiveData<JsonObject> {
        return engroFileUploadRepository.uploadFiles(filesList)
    }

}