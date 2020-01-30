package dev.dextra.newsapp.feature.news

import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel


class NewsViewModel(
    private val newsRepository: NewsRepository,
    private val newsActivity: NewsActivity
) : BaseViewModel() {

    private lateinit var source: Source
    val articles = MutableLiveData<MutableList<Article>>()
    var currentPage = 1
    var totalResults = 0


    fun configureSource(source: Source) {
        this.source = source
    }

    fun loadNews() {
        newsActivity.showLoading()
        addDisposable(
            newsRepository.getEverything(source.id, currentPage)
                .subscribe({ response ->
                    articles.postValue(response.articles.toMutableList())
                    setTotalResult(response.totalResults)
                    if (!isMaxResultsReached()) addPage()
                    newsActivity.hideLoading()
                }, {
                    newsActivity.hideLoading()
                })
        )
    }

    private fun addPage() {
        currentPage++
    }

    private fun setTotalResult(totalResults: Int) {
        this.totalResults = totalResults
    }

    fun isMaxResultsReached() = articles.value?.size == totalResults
}
