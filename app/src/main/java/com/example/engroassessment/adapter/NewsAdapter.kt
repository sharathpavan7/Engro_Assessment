package com.example.engroassessment.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.engroassessment.R
import com.example.engroassessment.model.ArticlesItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_news.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NewsAdapter(var mContext: Context,var articles: List<ArticlesItem>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = articles[position]
        if(data.urlToImage!=null && data.urlToImage.isNotEmpty()){
            Picasso.get().load(data.urlToImage).into(holder.itemView.iv_news_image)
        }
        holder.itemView.tv_date.text = getTime(data.publishedAt)
        holder.itemView.tv_title.text = data.title
        holder.itemView.tv_desc.text = data.description
        holder.itemView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
            mContext.startActivity(browserIntent)
        }
    }

    override fun getItemCount(): Int {
       return articles.size
    }

    @Throws(ParseException::class)
    fun getTime(date: String): String{
        var time = ""
        val arrCal = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale(Locale.getDefault().language))
        val depdate = format.parse(date)
        arrCal.time = depdate
        time = DateFormat.getDateTimeInstance().format(arrCal.time)
        return time
    }


}
