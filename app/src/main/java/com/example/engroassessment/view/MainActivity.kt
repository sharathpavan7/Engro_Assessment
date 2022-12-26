package com.example.engroassessment.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.engroassessment.R
import com.example.engroassessment.viewmodel.EngroFileUploadViewmodel
import kotlinx.android.synthetic.main.activity_upload.*


class MainActivity : AppCompatActivity() {

    companion object {
        val REQUEST_IMAGE_GALLERY = 1000
        val PERMISSION_REQUEST_CODE_STORAGE = 2000
    }

    val mArrayUri = ArrayList<Uri>()
    lateinit var engroFileUploadViewmodel: EngroFileUploadViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        engroFileUploadViewmodel = ViewModelProvider(this).get(EngroFileUploadViewmodel::class.java)

        initViews()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_GALLERY -> {
                mArrayUri.clear()
                val imagesEncodedList = ArrayList<String>()
                if (data?.data != null) {
                    val mImageUri: Uri? = data.data
                    mImageUri?.let {
                        mArrayUri.add(it)
                        val imageEncoded = getPath(it)
                        imagesEncodedList.add(imageEncoded)
                    }

                    if (mArrayUri.size > 0)
                        uploadFiles()
                } else if (data?.clipData != null) {
                    val clipDate = data.clipData
                    var clipDataItemCount = clipDate?.itemCount ?: 0
                    for (i in 0..(clipDataItemCount - 1)) {
                        clipDate?.let {
                            val item = it.getItemAt(i)
                            val uri = item.uri
                            mArrayUri.add(uri)
                            val imageEncoded = getPath(uri)
                            imagesEncodedList.add(imageEncoded)
                        }
                    }

                    if (mArrayUri.size > 0)
                        uploadFiles()
                } else {
                    Toast.makeText(
                        this, "You haven't picked Image",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE_STORAGE -> {
                launchGallery()
            }
        }
    }

    private fun uploadFiles() {
        engroFileUploadViewmodel.uploadFiles(mArrayUri)
    }


    private fun getPath(uri: Uri?): String {
        var path = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        path = cursor.getString(column_index)
        cursor.close()
        return path
    }
    private fun initViews() {
        uploadButton.setOnClickListener {
            if (handlePermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.storage_permission_rationale),
                    PERMISSION_REQUEST_CODE_STORAGE
                )
            ) {
                launchGallery()
            }
        }
    }

    private fun launchGallery() {
        val i = Intent()
        i.type = "image/*"
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(i, getString(R.string.select_image)),
            REQUEST_IMAGE_GALLERY
        )
    }

    fun handlePermission(permission: String?, rationale: String?, requestCode: Int): Boolean {
        if (hasPermissionGranted(permission)) {
            return true
        } else {
            requestPermission(permission, requestCode)
        }
        return false
    }

    fun hasPermissionGranted(permission: String?): Boolean {
        val result = ContextCompat.checkSelfPermission(this, permission!!)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String?, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

}