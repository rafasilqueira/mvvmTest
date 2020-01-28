package dev.dextra.newsapp.feature.news

import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel


class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val newsActivity: NewsActivity
) : BaseViewModel() {

    private lateinit var source: Source

    fun configureSource(source: Source) {
        this.source = source
    }

    fun loadNews() {
        newsActivity.showLoading()
        addDisposable(
            newsRepository.getEverything(source.id, newsActivity.currentPage)
                .subscribe(
                    { response ->
                        newsActivity.showData(response.articles.toMutableList())
                        newsActivity.totalResults = response.totalResults
                        newsActivity.hideLoading()
                    },
                    { newsActivity.hideLoading() })
        )
    }
}
