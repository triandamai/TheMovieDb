package space.trian.modulecommon.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
sealed class Either<out L, out R>() {
    @Serializable
    data class Left<T>(
        @SerialName("data") val data: T,
        @SerialName("message") val message: String = ""
    ) :
        Either<T, Nothing>()

    @Serializable
    data class Right<T>(@SerialName("data") val data: T) : Either<Nothing, T>()

    fun isLeft() = this is Left<L>
    fun isRight() = this is Right<R>

    companion object {
        fun <T> left(data: T, message: String = "") = Left(data, message)
        fun <T> right(data: T) = Right(data)
    }
}

fun <T> T.asSuccess(message: String = "") = Either.Left(this, message)
fun String.asError(code: Int) = Either.right(ErrorData(this, code))
fun Int.asError(message: String) = Either.right(ErrorData(message, this))


fun <R> Either<R, *>.leftOr(default: () -> R): R = when (this) {
    is Either.Left -> data
    is Either.Right -> default()
}

@Serializable
data class ErrorData(val message: String, val code: Int = 0)

fun <R> Either<*, R>.rightOr(default: () -> R): R = when (this) {
    is Either.Left -> default()
    is Either.Right -> data
}

fun <R> Either<*, *>.transform(cb: (Either<*, *>) -> R): R = cb(this)

