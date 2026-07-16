package space.trian.test

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import space.trian.modulecommon.di.commonModule
import space.trian.modulethemoviewdb.di.movieDbModule

class MainApplication: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                commonModule,
                movieDbModule
            )
        }
    }
}