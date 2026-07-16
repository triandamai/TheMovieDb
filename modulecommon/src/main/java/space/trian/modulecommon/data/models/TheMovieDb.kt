package space.trian.modulecommon.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponseGenre(
    val genres: List<Genre> = listOf(),
)
@Serializable
data class Genre(val id: Int, val name: String)


@Serializable
data class BasePaginationResponse<T>(
    @SerialName("page")
    val page: Int,
    @SerialName("results")
    val results: List<T>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)
@Serializable
data class Movie(
    val adult: Boolean,

    @SerialName("backdrop_path")
    val backdropPath: String?=null,

    @SerialName("genre_ids")
    val genreIDS: List<Long>,

    val id: Long,
    val title: String,

    @SerialName("original_language")
    val originalLanguage: String,

    @SerialName("original_title")
    val originalTitle: String,

    val overview: String,
    val popularity: Double,

    @SerialName("poster_path")
    val posterPath: String,

    @SerialName("release_date")
    val releaseDate: String,

    val softcore: Boolean,
    val video: Boolean,

    @SerialName("vote_average")
    val voteAverage: Double,

    @SerialName("vote_count")
    val voteCount: Long
)

@Serializable
data class Review(
    val id: String,
    val author: String,
    @SerialName("author_details")
    val authorDetails: AuthorDetails,
    val content: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    val url: String
)

@Serializable
data class AuthorDetails(
    val name: String,
    val username: String,
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    val rating: Double? = null
)

@Serializable
data class VideoResponse(
    val id: Int,
    val results: List<Video>
)

@Serializable
data class Video(
    val id: String,
    @SerialName("iso_639_1")
    val iso6391: String,
    @SerialName("iso_3166_1")
    val iso31661: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)
