package space.trian.modulethemoviewdb.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import space.trian.modulethemoviewdb.feature.discover.DiscoverViewModel
import space.trian.modulethemoviewdb.feature.home.HomeViewModel
import space.trian.modulethemoviewdb.feature.reviews.ReviewsViewModel

val movieDbModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DiscoverViewModel)
    viewModelOf(::ReviewsViewModel)
}
