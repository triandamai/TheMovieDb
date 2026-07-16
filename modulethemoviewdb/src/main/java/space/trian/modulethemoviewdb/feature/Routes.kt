package space.trian.modulethemoviewdb.feature

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.viewmodel.koinViewModel
import space.trian.modulethemoviewdb.feature.discover.DiscoverScreen
import space.trian.modulethemoviewdb.feature.discover.DiscoverViewModel
import space.trian.modulethemoviewdb.feature.home.HomeScreen
import space.trian.modulethemoviewdb.feature.home.HomeViewModel
import space.trian.modulethemoviewdb.feature.reviews.ReviewsScreen
import space.trian.modulethemoviewdb.feature.reviews.ReviewsViewModel
import space.trian.modulethemoviewdb.feature.splash.SplashScreen


@Serializable
data object MovieDbSplash : NavKey

@Serializable
data object MovieDbHome : NavKey

@Serializable
data class MovieDbDiscover(val genre: Int) : NavKey

@Serializable
data class MovieDbReviews(val movieId: Long) : NavKey

val movieRouteConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(MovieDbSplash::class, MovieDbSplash.serializer())
            subclass(MovieDbHome::class, MovieDbHome.serializer())
            subclass(MovieDbDiscover::class, MovieDbDiscover.serializer())
            subclass(MovieDbReviews::class, MovieDbReviews.serializer())
        }
    }
}


fun EntryProviderScope<NavKey>.theMovieDbNav(backStack: NavBackStack<NavKey>) {
    entry<MovieDbSplash> {
        SplashScreen(backStack = backStack)
    }
    entry<MovieDbHome> {
        val viewModel = koinViewModel<HomeViewModel>()
        HomeScreen(
            backStack = backStack,
            genres = viewModel.genres,
            state = viewModel.uiState,
            event = viewModel::addOnEventListener,
            action = viewModel::invokeAction
        )
    }
    entry<MovieDbDiscover> { key ->
        val viewModel = koinViewModel<DiscoverViewModel>()
        DiscoverScreen(
            backStack = backStack,
            genreId = key.genre,
            movies = viewModel.movies,
            state = viewModel.uiState,
            event = viewModel::addOnEventListener,
            action = viewModel::invokeAction
        )
    }
    entry<MovieDbReviews> { key ->
        val viewModel = koinViewModel<ReviewsViewModel>()
        ReviewsScreen(
            movieId = key.movieId,
            backStack = backStack,
            state = viewModel.uiState,
            action = viewModel::invokeAction
        )
    }
}

