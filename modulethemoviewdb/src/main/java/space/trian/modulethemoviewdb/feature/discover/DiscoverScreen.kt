package space.trian.modulethemoviewdb.feature.discover

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.fillMaxSize
import space.trian.modulecommon.data.models.Movie
import space.trian.modulethemoviewdb.components.BottomSheetDetailMovie
import space.trian.modulethemoviewdb.feature.MovieDbReviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    genreId: Int?,
    backStack: NavBackStack<NavKey>,
    state: Flow<DiscoverState>,
    movies: SnapshotStateList<Movie>,
    event: (suspend (DiscoverEvent) -> Unit) -> Unit,
    action: (DiscoverAction) -> Unit
) {
    val uiState by state.collectAsState(DiscoverState())
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val bottomSheerState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    event { e ->
        when (e) {
            DiscoverEvent.ShowBottomSheet -> {
                showBottomSheet = true
                scope.launch { bottomSheerState.show() }
            }

            is DiscoverEvent.NavigateToReviews -> {
                backStack.add(MovieDbReviews(e.movieId))
            }
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }


    LaunchedEffect(genreId) {
        if (genreId != null) {
            action(DiscoverAction.LoadMovie(genreId))
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        Log.e("SHOULD MORE", shouldLoadMore.value.toString())

        if (shouldLoadMore.value && genreId != null && !uiState.loading) {
            action(DiscoverAction.LoadMore(genreId))
        }
    }

    if (showBottomSheet) {
        BottomSheetDetailMovie(
            movie = uiState.detailMovie,
            reviews = uiState.reviews,
            trailerKey = uiState.trailerKey,
            sheetState = bottomSheerState,
            onDismissRequest = {
                scope.launch { bottomSheerState.hide() }.invokeOnCompletion {
                    showBottomSheet = false
                }
            },
            onViewAllReviews = {
                action(DiscoverAction.ViewAllReviews)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discover") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeAt(backStack.lastIndex) }) {
                        Icon(
                            painter = painterResource(com.composables.icons.feather.R.drawable.feather_ic_arrow_left),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.loading && movies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (movies.isEmpty() && !uiState.loading) {
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
                        "No movies found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->
                    MovieRowItem(movie = movie, onClick = { action(DiscoverAction.SelectMovie(movie)) })
                }

                if (uiState.loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieRowItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                if (movie.posterPath.isNotEmpty()) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("No Poster", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Released: ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(com.composables.icons.feather.R.drawable.feather_ic_star),
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = movie.popularity.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}