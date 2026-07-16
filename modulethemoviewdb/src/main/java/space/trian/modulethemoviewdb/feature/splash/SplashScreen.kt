package space.trian.modulethemoviewdb.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.delay
import space.trian.modulethemoviewdb.feature.MovieDbHome

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>
) {
    LaunchedEffect(Unit) {
        delay(2000)
        backStack.removeAt(backStack.lastIndex)
        backStack.add(MovieDbHome)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(com.composables.icons.feather.R.drawable.feather_ic_play),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )
    }
}
