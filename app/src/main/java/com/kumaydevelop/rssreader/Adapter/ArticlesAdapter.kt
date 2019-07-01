package com.kumaydevelop.rssreader.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kumaydevelop.rssreader.Entity.BlogDetailEntity
import com.kumaydevelop.rssreader.R
import java.text.SimpleDateFormat
import java.util.*


class ArticlesAdapter(private val context: Context,
                      private val articles: ArrayList<BlogDetailEntity>,
                      private val articleClicked: (BlogDetailEntity) -> Unit) : RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(view: View, val articleClicked: (BlogDetailEntity) -> Unit): RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.titleView)
        val date = view.findViewById<TextView>(R.id.dateView)
        val url = view.findViewById<TextView>(R.id.urlView)

        fun setUp(article: BlogDetailEntity) {
            this.itemView.setOnClickListener { articleClicked(article) }
        }
    }

    override fun getItemCount(): Int = articles.size

    //1行表示するたびに呼ばれレイアウトを生成してViewHolderを生成して返す
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val rowView = LayoutInflater.from(context).inflate(R.layout.article_content, parent, false)

        return ArticleViewHolder(rowView, articleClicked)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder?, position: Int) {
        holder!!.let {
            it.title.text = articles[position].title
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
            val formatDate = formatter.parse(articles[position].date)
            it.date.text = DateFormat.format("yyyy/MM/dd HH:mm",formatDate).toString()
            it.url.text = articles[position].link
        }

        holder.setUp(articles[position])
    }
}