package com.example.engroassessment.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.engroassessment.R
import com.example.engroassessment.adapter.NewsAdapter
import com.example.engroassessment.model.NewsResponse
import com.example.engroassessment.viewmodel.NewsFeedViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    companion object {
        val PERMISSION_REQUEST_CODE_STORAGE = 2000
        val REQUEST_IMAGE_GALLERY = 1000
    }

    val mArrayUri = ArrayList<Uri>()
    lateinit var newsViewModel: NewsFeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        newsViewModel = ViewModelProvider(this).get(NewsFeedViewModel::class.java)

        getNews()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_upload -> {
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
        return super.onOptionsItemSelected(item)
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
        newsViewModel.uploadFiles(mArrayUri)
    }

    private fun handlePermission(permission: String?, rationale: String?, requestCode: Int): Boolean {
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

    private fun requestPermission(permission: String?, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

    private fun getNews() {
        newsViewModel.getNews().observe(this, Observer { response->
            if(response!=null){
                var newsResponse = Gson().fromJson(response, NewsResponse::class.java)
                setAdapter(newsResponse)
            }
        })
    }

    private fun setAdapter(newsResponse: NewsResponse) {
        if(newsResponse.articles!=null && newsResponse.articles.size>0){
            rv_news.layoutManager = LinearLayoutManager(this)
            rv_news.adapter = NewsAdapter(this, newsResponse.articles)
        }
    }
}