package space.trian.modulecommon.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import space.trian.modulecommon.arch.safeApiCall
import space.trian.modulecommon.data.models.BaseResponseGenre
import space.trian.modulecommon.data.models.BasePaginationResponse
import space.trian.modulecommon.data.models.Movie
import space.trian.modulecommon.data.models.Outcome
import space.trian.modulecommon.data.models.Review
import space.trian.modulecommon.data.models.VideoResponse


interface MovieRemoteDataSource {
    fun getGenres(): Flow<Outcome<BaseResponseGenre>>
    fun getMoviesByGenre(genreId: Int, page: Int): Flow<Outcome<BasePaginationResponse<Movie>>>
    fun getMovieReviews(movieId: Int, page: Int): Flow<Outcome<BasePaginationResponse<Review>>>
    fun getMovieVideos(movieId: Int): Flow<Outcome<VideoResponse>>
}

class MovieRemoteDataSourceImpl(private val client: HttpClient) : MovieRemoteDataSource {

    override fun getGenres(): Flow<Outcome<BaseResponseGenre>> = flow {
        emit(Outcome.Loading)
        val result = safeApiCall<BaseResponseGenre> {
            client.get("/3/genre/movie/list")
        }
        emit(result)
    }

    override fun getMoviesByGenre(genreId: Int, page: Int): Flow<Outcome<BasePaginationResponse<Movie>>> = flow {
        emit(Outcome.Loading)
        val result = safeApiCall<BasePaginationResponse<Movie>> {
            client.get("/3/discover/movie") {
                parameter("with_genres", genreId)
                parameter("page", page)
            }
        }
        emit(result)
    }

    override fun getMovieReviews(movieId: Int, page: Int): Flow<Outcome<BasePaginationResponse<Review>>> = flow {
        emit(Outcome.Loading)
        val result = safeApiCall<BasePaginationResponse<Review>> {
            client.get("/3/movie/$movieId/reviews") {
                parameter("page", page)
            }
        }
        emit(result)
    }

    override fun getMovieVideos(movieId: Int): Flow<Outcome<VideoResponse>> = flow {
        emit(Outcome.Loading)
        val result = safeApiCall<VideoResponse> {
            client.get("/3/movie/$movieId/videos")
        }
        emit(result)
    }
}
