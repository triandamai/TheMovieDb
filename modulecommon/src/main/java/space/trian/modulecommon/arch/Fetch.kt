package space.trian.modulecommon.arch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Fetch<out Out> {
    @Serializable
    data class Present<A>(@SerialName("value") val value: A, val message: String = "") : Fetch<A>()

    @Serializable
    data class Failure<E>(@SerialName("error") val error: String) : Fetch<E>()

    @Serializable
    data object Loading : Fetch<Nothing>()

    fun isLoading(): Boolean = this is Loading
    fun isFailure(): Boolean = this is Failure
    fun isPresent(): Boolean = this is Present<Out>

    companion object {
        fun absent(): Fetch<Nothing> = Loading
        fun <T> present(value: T, message: String = ""): Fetch<T> = Present(value, message)
        fun <T> failure(error: String): Fetch<T> = Failure(error)
    }
}