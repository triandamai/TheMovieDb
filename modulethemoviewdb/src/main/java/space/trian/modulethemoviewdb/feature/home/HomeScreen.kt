package space.trian.modulethemoviewdb.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import space.trian.modulecommon.data.models.Genre
import space.trian.modulethemoviewdb.feature.MovieDbDiscover

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey>,
    state: Flow<HomeState>,
    genres: SnapshotStateList<Genre>,
    event: (suspend (HomeEvent) -> Unit) -> Unit,
    action: (HomeAction) -> Unit
) {

    val uiState by state.collectAsState(HomeState())
    val lazyState = rememberLazyGridState()

    LaunchedEffect(event) {
        event {

        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MovieDB"
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(
                            painter = painterResource(com.composables.icons.feather.R.drawable.feather_ic_search),
                            contentDescription = "Search"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (genres.isEmpty() && !uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(com.composables.icons.feather.R.drawable.feather_ic_inbox),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No genres available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = lazyState,
                modifier = modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(genres, key = { it.id }) { genre ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable {
                                backStack.add(MovieDbDiscover(genre.id))
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = genre.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (uiState.loading) {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 10.dp,
                                    horizontal = 10.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Load more data...")
                            CircularProgressIndicator()
                        }
                    }
                }
                if (!uiState.loading && (uiState.errorLoad != null)) {
                    item(span = { GridItemSpan(2) }) {
                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 10.dp,
                                    horizontal = 10.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Unable to fetch ${uiState.errorLoad}")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Genre List Screen")
@Composable
fun PreviewGenreListScreen() {
    val genres = SnapshotStateList<Genre>()
    genres.addAll(
        listOf(
            Genre(28, "Action"),
            Genre(12, "Adventure"),
            Genre(35, "Comedy"),
            Genre(80, "Crime"),
            Genre(18, "Drama"),
            Genre(14, "Fantasy")
        )
    )
    MaterialTheme {
        Surface {
            HomeScreen(
                genres = genres,
                backStack = rememberNavBackStack(),
                state = flowOf(),
                event = {},
                action = {}
            )
        }
    }
}