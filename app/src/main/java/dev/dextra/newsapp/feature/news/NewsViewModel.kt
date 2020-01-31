package dev.dextra.newsapp.feature.news

import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Article
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState


class NewsViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    private lateinit var source: Source
    val currentArticles = MutableLiveData<MutableList<Article>>()
    val networkState = MutableLiveData<NetworkState>()
    var currentPage = 1
    var totalResults = 0
    var currentResults: Int = 0


    fun configureSource(source: Source) {
        this.source = source
    }

    init {
        currentArticles.value = ArrayList()
    }

    fun loadNews() {
        networkState.postValue(NetworkState.RUNNING)
        addDisposable(
            newsRepository.getEverything(source.id, currentPage)
                .subscribe({ response ->
                    val serverArticles = response.articles.toMutableList()
                    currentArticles.postValue(serverArticles)

                    updateCurrentResults(serverArticles.size)
                    setTotalResult(response.totalResults)

                    if (!isMaxResultsReached()) addPage()

                    setupNetworkState()
                }, {
                    setupNetworkState()
                })
        )
    }

    private fun addPage() {
        currentPage++
    }

    private fun setTotalResult(totalResults: Int) {
        this.totalResults = totalResults
    }

    private fun setupNetworkState() {
        if (currentResults == 0) {
            networkState.postValue(NetworkState.ERROR)
        } else {
            networkState.postValue(NetworkState.SUCCESS)
        }


    }

    private fun updateCurrentResults(results: Int) {
        currentResults += results
    }

    fun isMaxResultsReached() = currentResults >= totalResults
}
