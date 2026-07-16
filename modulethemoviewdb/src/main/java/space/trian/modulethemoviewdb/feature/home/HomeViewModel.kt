package space.trian.modulethemoviewdb.feature.home

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import space.trian.modulecommon.arch.BaseViewModel
import space.trian.modulecommon.data.models.BaseResponseGenre
import space.trian.modulecommon.data.models.Genre
import space.trian.modulecommon.data.models.Outcome
import space.trian.modulecommon.data.remote.MovieRemoteDataSource

data class HomeState(
    val loading: Boolean = false,
    val errorLoad: String? = null,
);


sealed interface HomeAction {
    data object NextPage : HomeAction

    data object LoadGenres : HomeAction
}

sealed interface HomeEvent {

}

class HomeViewModel(
    private val movieDbRemoteDataSource: MovieRemoteDataSource
) : BaseViewModel<HomeState, HomeAction, HomeEvent>(
    HomeState(false)
) {
    val genres: SnapshotStateList<Genre> = SnapshotStateList()

    override fun doInit() {
        fetchGenre()
    }

    override fun doRefreshInit() {

    }

    override fun onAction() {
        on(HomeAction.LoadGenres::class) {
            fetchGenre()
        }
        on(HomeAction.NextPage::class) {
            fetchGenre()
        }
    }

    private fun fetchGenre() = viewModelScope.launch {

        movieDbRemoteDataSource.getGenres().onEach { response ->
            when (response) {
                is Outcome.Error -> commit {
                    copy(
                        loading = false,
                        errorLoad = response.message
                    )
                }

                Outcome.Loading -> commit {
                    copy(loading = true, errorLoad = null)
                }

                is Outcome.Success<BaseResponseGenre> -> {

                    genres.addAll(response.data.genres)

                    commit {
                        copy(
                            loading = false,
                            errorLoad = null
                        )
                    }

                    Log.e("GENRES", response.data.genres.joinToString { it.toString() })
                }
            }
        }.catch {
            commit {
                copy(
                    loading = false,
                    errorLoad = it.message
                )
            }
        }.collect()
    }
}