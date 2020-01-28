package dev.dextra.newsapp.feature.sources

import androidx.lifecycle.MutableLiveData
import dev.dextra.newsapp.api.model.Source
import dev.dextra.newsapp.api.model.enums.Category
import dev.dextra.newsapp.api.model.enums.Country
import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.BaseViewModel
import dev.dextra.newsapp.base.NetworkState

class SourcesViewModel(private val newsRepository: NewsRepository) : BaseViewModel() {

    val sources = MutableLiveData<List<Source>>()
    val networkState = MutableLiveData<NetworkState>()

    private var selectedCountry: Country? = null
    private var selectedCategory: Category? = null

    fun loadSources() {
        networkState.postValue(NetworkState.RUNNING)
        addDisposable(
            newsRepository.getSources(
                selectedCountry?.name?.toLowerCase() ?: Country.ALL.name,
                selectedCategory?.name?.toLowerCase() ?: Category.ALL.name
            )
                .subscribe({
                    sources.postValue(it.sources)
                    if (it.sources.isEmpty()) {
                        networkState.postValue(NetworkState.ERROR)
                    } else {
                        networkState.postValue(NetworkState.SUCCESS)
                    }
                }, {
                    networkState.postValue(NetworkState.ERROR)
                })
        )
    }

    fun changeCountry(country: Country?) {
        selectedCountry = if (Country.ALL == country) null else country
        loadSources()
    }

    fun changeCategory(category: Category) {
        selectedCategory = if (Category.ALL == category) null else category
        loadSources()
    }
}
