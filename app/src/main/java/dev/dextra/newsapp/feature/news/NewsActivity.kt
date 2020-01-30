package dev.dextra.newsapp.feature.news

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.repository.EndpointService
import dev.dextra.newsapp.feature.news.adapter.ArticleAdapter
import dev.dextra.newsapp.feature.news.adapter.OnAdapterClick
import kotlinx.android.synthetic.main.activity_news.*

const val NEWS_ACTIVITY_SOURCE = "NEWS_ACTIVITY_SOURCE"

class NewsActivity : AppCompatActivity(), OnAdapterClick {

    private val newsViewModel = NewsViewModel(NewsRepository(EndpointService()), this)
    private val articleAdapter = ArticleAdapter(this@NewsActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_news)

        (intent?.extras?.getSerializable(NEWS_ACTIVITY_SOURCE) as Source).let { source ->
            title = source.name
            showData()
            loadNews(source)
            news_list.adapter = articleAdapter
        }

        news_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if (!newsViewModel.isMaxResultsReached()) {
                        newsViewModel.loadNews()
                    }
                }
            }
        })

        super.onCreate(savedInstanceState)

    }

    private fun loadNews(source: Source) {
        newsViewModel.configureSource(source)
        newsViewModel.loadNews()
    }

    override fun onClick(article: Article) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(article.url)
        startActivity(i)
    }

    private var loading: Dialog? = null

    fun showLoading() {
        if (loading == null) {
            loading = Dialog(this)
            loading?.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setContentView(R.layout.dialog_loading)
            }
        }
        loading?.show()
    }

    fun hideLoading() {
        loading?.dismiss()
    }

    private fun showData() {
        newsViewModel.articles.observe(this, Observer {
            articleAdapter.apply {
                add(it)
            }
        })
    }
}
