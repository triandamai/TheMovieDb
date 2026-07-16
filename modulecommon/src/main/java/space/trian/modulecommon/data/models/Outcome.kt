package space.trian.modulecommon.data.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Outcome<out T> {

    @Serializable
    data object Loading : Outcome<Nothing>()

    @Serializable
    data class Success<out T>(val data: T) : Outcome<T>()

    @Serializable
    data class Error(val message: String, val statusCode: Int? = null) : Outcome<Nothing>()
}