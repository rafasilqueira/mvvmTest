package dev.dextra.newsapp.base

import dev.dextra.newsapp.api.repository.NewsRepository
import dev.dextra.newsapp.base.repository.EndpointService
import dev.dextra.newsapp.feature.news.NewsActivity
import dev.dextra.newsapp.feature.news.NewsViewModel
import dev.dextra.newsapp.feature.sources.SourcesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module


val viewModelModules = module {
    factory { SourcesViewModel(get()) }
    factory { NewsViewModel(NewsRepository(EndpointService())) }
}

val serviceModules = module {
    single { EndpointService() }
}

val repositoryModules = module {
    single { NewsRepository(get()) }
}



val appComponent: List<Module> = listOf(viewModelModules, serviceModules, repositoryModules)