package dev.dextra.newsapp.feature.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import dev.dextra.newsapp.R
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.base.BaseListActivity
import dev.dextra.newsapp.feature.news.adapter.ArticleAdapter
import dev.dextra.newsapp.feature.news.adapter.OnAdapterClick
import kotlinx.android.synthetic.main.activity_news.*
import org.koin.android.ext.android.inject

const val NEWS_ACTIVITY_SOURCE = "NEWS_ACTIVITY_SOURCE"

class NewsActivity : BaseListActivity(), OnAdapterClick {

    override val emptyStateTitle: Int = R.string.empty_state_title_source
    override val emptyStateSubTitle: Int = R.string.empty_state_subtitle_source
    override val errorStateTitle: Int = R.string.error_state_title_source
    override val errorStateSubTitle: Int = R.string.error_state_subtitle_source
    override val mainList: View get() = news_list

    //private val newsViewModel = NewsViewModel(NewsRepository(EndpointService()), this)
    private val newsViewModel: NewsViewModel by inject()
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

    private fun showData() {
        newsViewModel.articles.observe(this, Observer {
            articleAdapter.apply {
                add(it)
            }
        })

        newsViewModel.networkState.observe(this, networkStateObserver)
    }

    override fun setupLandscape() {

    }

    override fun setupPortrait() {

    }

    override fun executeRetry() {

    }

}
