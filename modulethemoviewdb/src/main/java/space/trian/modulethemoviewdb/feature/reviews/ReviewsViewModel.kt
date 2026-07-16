package space.trian.modulethemoviewdb.feature.reviews

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.trian.modulecommon.arch.BaseViewModel
import space.trian.modulecommon.data.models.BasePaginationResponse
import space.trian.modulecommon.data.models.Outcome
import space.trian.modulecommon.data.models.Review
import space.trian.modulecommon.data.remote.MovieRemoteDataSource

data class ReviewsState(
    val loading: Boolean = false,
    val reviews: List<Review> = listOf(),
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val totalItems: Int = 0
)

sealed interface ReviewsAction {
    data class LoadReviews(val movieId: Long) : ReviewsAction
    data class LoadMore(val movieId: Long) : ReviewsAction
}

sealed interface ReviewsEvent

class ReviewsViewModel(
    private val movieDbRemoteDataSource: MovieRemoteDataSource
) : BaseViewModel<ReviewsState, ReviewsAction, ReviewsEvent>(
    ReviewsState()
) {
    override fun doInit() {}

    override fun doRefreshInit() {}

    override fun onAction() {
        on(ReviewsAction.LoadReviews::class) { action ->
            fetchReviews(action.movieId, false)
        }
        on(ReviewsAction.LoadMore::class) { action ->
            fetchReviews(action.movieId, true)
        }
    }

    private fun fetchReviews(movieId: Long, loadMore: Boolean) = viewModelScope.launch {
        val state = uiState.value
        val page = if (loadMore) state.currentPage + 1 else 1

        movieDbRemoteDataSource.getMovieReviews(movieId.toInt(), page).collect { response ->
            when (response) {
                is Outcome.Error -> commit { copy(loading = false, errorMessage = response.message) }
                Outcome.Loading -> commit { copy(loading = true, errorMessage = null) }
                is Outcome.Success<BasePaginationResponse<Review>> -> {
                    val currentReviews = if (loadMore) state.reviews + response.data.results else response.data.results
                    commit {
                        copy(
                            loading = false,
                            reviews = currentReviews,
                            currentPage = response.data.page,
                            totalPages = response.data.totalPages,
                            totalItems = response.data.totalResults
                        )
                    }
                }
            }
        }
    }
}
