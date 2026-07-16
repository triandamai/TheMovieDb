package space.trian.modulethemoviewdb.feature.discover

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import space.trian.modulecommon.arch.BaseViewModel
import space.trian.modulecommon.data.models.BasePaginationResponse
import space.trian.modulecommon.data.models.Movie
import space.trian.modulecommon.data.models.Outcome
import space.trian.modulecommon.data.models.Review
import space.trian.modulecommon.data.remote.MovieRemoteDataSource

data class DiscoverState(
    val loading: Boolean = false,

    val currentPage: Int = 1,
    val totalItems: Int = 0,
    val totalPages: Int = 0,

    val errorLoad: String? = null,

    val detailMovie: Movie? = null,
    val reviews: List<Review> = listOf(),
    val trailerKey: String? = null
)

sealed interface DiscoverEvent {
    data object ShowBottomSheet : DiscoverEvent
    data class NavigateToReviews(val movieId: Long) : DiscoverEvent
}

sealed interface DiscoverAction {
    data class LoadMovie(val genre: Int) : DiscoverAction
    data class LoadMore(val genre: Int) : DiscoverAction
    data class SelectMovie(val movie: Movie) : DiscoverAction
    data object ViewAllReviews : DiscoverAction
}

class DiscoverViewModel(
    private val movieDbRemoteDataSource: MovieRemoteDataSource
) : BaseViewModel<DiscoverState, DiscoverAction, DiscoverEvent>(
    DiscoverState()
) {

    val movies: SnapshotStateList<Movie> = SnapshotStateList()
    override fun doInit() {

    }

    override fun doRefreshInit() {

    }

    override fun onAction() {
        on(DiscoverAction.LoadMovie::class) { id ->
            fetchMovie(id.genre, false)
        }
        on(DiscoverAction.LoadMore::class) { id ->
            fetchMovie(id.genre, true)
        }
        on(DiscoverAction.SelectMovie::class) { action ->
            showDetailMovie(action.movie)
        }
        on(DiscoverAction.ViewAllReviews::class) {
            val state = uiState.value
            state.detailMovie?.let {
                send(DiscoverEvent.NavigateToReviews(it.id))
            }
        }
    }

    private fun fetchMovie(genreId: Int, loadMore: Boolean) = viewModelScope.launch {
        val state = uiState.value

        val nextPage = if (loadMore) state.currentPage + 1 else 1
        Log.e("NEXT PAGE", nextPage.toString())
        movieDbRemoteDataSource.getMoviesByGenre(genreId, nextPage).onEach { response ->
            when (response) {
                is Outcome.Error -> commit { copy(loading = false, errorLoad = response.message) }
                Outcome.Loading -> {
                    if (!loadMore) movies.clear()
                    commit { copy(loading = true, errorLoad = null) }
                }
                is Outcome.Success<BasePaginationResponse<Movie>> -> {
                    movies.addAll(response.data.results)
                    commit {
                        copy(
                            loading = false,
                            errorLoad = null,
                            currentPage = response.data.page,
                            totalPages = response.data.totalPages,
                            totalItems = response.data.totalResults
                        )
                    }
                }
            }
        }.catch {
            commit { copy(loading = false,errorLoad = null) }
        }.collect()
    }

    private fun fetchReviews(movieId: Long) = viewModelScope.launch {
        movieDbRemoteDataSource.getMovieReviews(movieId.toInt(), 1).collect { response ->
            when (response) {
                is Outcome.Error -> commit { copy(errorLoad = response.message) }
                Outcome.Loading -> {}
                is Outcome.Success<BasePaginationResponse<Review>> -> {
                    commit {
                        copy(
                            reviews = response.data.results
                        )
                    }
                }
            }
        }
    }

    private fun fetchVideos(movieId: Long) = viewModelScope.launch {
        movieDbRemoteDataSource.getMovieVideos(movieId.toInt()).collect { response ->
            when (response) {
                is Outcome.Error -> commit { copy(errorLoad = response.message) }
                Outcome.Loading -> {}
                is Outcome.Success -> {
                    val trailer = response.data.results.find {
                        it.site == "YouTube" && it.type == "Trailer"
                    }
                    Log.e("TR",trailer.toString())
                    commit {
                        copy(
                            trailerKey = trailer?.key
                        )
                    }
                }
            }
        }
    }

    private fun showDetailMovie(movie: Movie) {
        commit {
            copy(
                detailMovie = movie,
                trailerKey = null,
                reviews = listOf()
            )
        }
        fetchReviews(movie.id)
        fetchVideos(movie.id)
        send(DiscoverEvent.ShowBottomSheet)
    }
}
