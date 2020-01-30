package dev.dextra.newsapp.feature.news.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.feature.sources.adapter.AbstractAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArticleAdapter(
    private val mContext: Context,
    private val articles: MutableList<Article> = ArrayList()
) : AbstractAdapter<Article>(mContext, articles) {

    private val df = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
    private val parseFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    override fun setupViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VHArticle(
            LayoutInflater.from(context).inflate(
                R.layout.item_article,
                parent,
                false
            )
        )
    }

    override fun onBindData(holder: RecyclerView.ViewHolder, genericType: Article) {
        if (holder is VHArticle) {
            holder.setIsRecyclable(false)
            genericType.apply {
                holder.articleName.text = title
                holder.articleDescription.text = description
                holder.articleAuthor.text = author
                holder.articleDate.text = df.format(parseFormat.parse(publishedAt))
                holder.itemView.setOnClickListener {
                    if (mContext is OnAdapterClick) mContext.onClick(this)
                }
                urlToImage?.let {
                    Picasso.get()
                        .load(it)
                        .fit()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.ic_worldwide_news)
                        .into(holder.articleImg)
                }
            }
        }
    }

    fun add(serverArticles: MutableList<Article>) {
        if (serverArticles.isNotEmpty()) {
            this.articles.addAll(serverArticles)
            notifyDataSetChanged()
        }
    }
}

interface OnAdapterClick {
    fun onClick(article: Article)
}

class VHArticle(view: View) : RecyclerView.ViewHolder(view) {
    val articleName: TextView = view.findViewById(R.id.article_name)
    val articleDescription: TextView = view.findViewById(R.id.article_description)
    val articleAuthor: TextView = view.findViewById(R.id.article_author)
    val articleDate: TextView = view.findViewById(R.id.article_date)
    val articleImg: AppCompatImageView = view.findViewById(R.id.article_img)
}