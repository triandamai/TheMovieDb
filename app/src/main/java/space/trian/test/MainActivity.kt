package space.trian.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import space.trian.modulethemoviewdb.feature.MovieDbHome
import space.trian.modulethemoviewdb.feature.MovieDbSplash
import space.trian.modulethemoviewdb.feature.movieRouteConfig
import space.trian.modulethemoviewdb.feature.theMovieDbNav
import space.trian.test.ui.theme.TrianSpaceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrianSpaceTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val backStack = rememberNavBackStack(movieRouteConfig, MovieDbSplash)

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            theMovieDbNav(backStack)
        }
    )
}