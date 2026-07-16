package space.trian.modulecommon.arch

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import space.trian.modulecommon.BuildConfig
import space.trian.modulecommon.data.models.Outcome
import java.net.UnknownHostException

fun getHttpClient(): HttpClient =
    HttpClient(OkHttp) {
        defaultRequest {
            url("https://api.themoviedb.org")
            header(
                HttpHeaders.Authorization,
                "Bearer ${BuildConfig.API_KEY}"
            )
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }


suspend inline fun <reified T> safeApiCall(
    crossinline cb: suspend () -> HttpResponse
): Outcome<T> {
    return try {
        val response = cb.invoke()

        Log.e("HEHE", response.toString());
        if (response.status.value in 200..299) {
            Outcome.Success(response.body<T>())
        } else {
            Outcome.Error(
                message = "Server returned error status",
                statusCode = response.status.value
            )
        }
    } catch (e: ResponseException) {
        e.printStackTrace()
        Outcome.Error("HTTP Error: ${e.localizedMessage}", e.response.status.value)
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        Outcome.Error("No internet connection available")
    } catch (e: IOException) {
        e.printStackTrace()
        Outcome.Error("Network communication failed: ${e.localizedMessage}")
    } catch (e: Exception) {
        e.printStackTrace()
        Outcome.Error("An unexpected error occurred: ${e.localizedMessage}")
    }
}