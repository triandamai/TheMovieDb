package space.trian.modulecommon.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import space.trian.modulecommon.arch.getHttpClient
import space.trian.modulecommon.data.remote.MovieRemoteDataSource
import space.trian.modulecommon.data.remote.MovieRemoteDataSourceImpl

val commonModule = module {
    single<HttpClient> {
        getHttpClient()
    }

    single<MovieRemoteDataSource> {
        MovieRemoteDataSourceImpl(get())
    }
}