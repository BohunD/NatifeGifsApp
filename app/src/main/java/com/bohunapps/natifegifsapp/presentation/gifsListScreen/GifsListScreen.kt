package com.bohunapps.natifegifsapp.presentation.gifsListScreen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.bohunapps.natifegifsapp.R
import com.bohunapps.natifegifsapp.domain.model.GifImage
import com.bohunapps.natifegifsapp.presentation.gifFullScreen.GifFullScreen
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel.GifViewModel
import com.bohunapps.natifegifsapp.presentation.gifsListScreen.viewmodel.GifsListContract
import com.bohunapps.natifegifsapp.presentation.util.ImageLoadingManager
import com.bohunapps.natifegifsapp.ui.mvi.use
import java.io.File
import java.lang.Error

@Composable
fun GifsListScreenRoute(
    viewModel: GifViewModel,
    onGifClicked: (GifImage) -> Unit,
) {
    val (state, event) = use(viewModel)
    GifsListScreen(state, event, onGifClicked = { onGifClicked(it) })
}

@Composable
fun GifsListScreen(
    state: GifsListContract.State,
    event: (GifsListContract.Event) -> Unit,
    onGifClicked: (GifImage) -> Unit,
) {
    val gifs: LazyPagingItems<GifImage> = state.gifsListFlow.collectAsLazyPagingItems()
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(state.currentQuery) {
            event(GifsListContract.Event.SetQuery(it))
        }
        GifList(gifs, event, onGifClicked = {
            onGifClicked(it)
        })
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search GIFs") },
        maxLines = 1
    )
}

@Composable
fun GifList(
    gifs: LazyPagingItems<GifImage>?,
    event: (GifsListContract.Event) -> Unit,
    onGifClicked: (GifImage) -> Unit,
) {
    gifs?.let {
        Box(modifier = Modifier.fillMaxSize()) {

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(3)
            ) {
                items(
                    count = gifs.itemCount,
                ) { index ->
                    gifs[index]?.let { gif ->
                        GifItem(
                            gif = gif,
                            onClick = {
                                onGifClicked(it)
                            },
                            onDelete = { event(GifsListContract.Event.BanGif(it)) },
                            onSave = {
                                event(GifsListContract.Event.SaveGif(it))
                            },
                            onError = { event(GifsListContract.Event.RemoveGifFromSaved(it)) }
                        )
                    }
                }
                item { HandleLoadState(gifs) }
            }
        }
    }
}

@Composable
fun HandleLoadState(gifs: LazyPagingItems<GifImage>) {
    gifs.apply {
        when (loadState.append) {
            is LoadState.Loading -> LoadingIndicator()
            is LoadState.Error -> ShowErrorToast(stringResource(R.string.can_not_load_more_check_your_internet_connection))
            is LoadState.NotLoading -> {}
        }
    }
}

@Composable
fun ShowErrorToast(errorText: String) {
    Toast.makeText(LocalContext.current, "Error: ${errorText}", Toast.LENGTH_LONG).show()
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun GifItem(
    gif: GifImage,
    onClick: (GifImage) -> Unit,
    onDelete: (GifImage) -> Unit,
    onError: (GifImage) -> Unit,
    onSave: (GifImage) -> Unit,
) {

    Box(
        Modifier
            .padding(4.dp)
            .fillMaxWidth(0.9f)
            .height(200.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colorResource(id = R.color.isabeline)),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        val imageRequest =
            ImageLoadingManager.getRequest(
                context,
                gif.images.original.url,
                onError = { onError(gif) },
                onSuccess = { onSave(gif) }
            )

        val imageLoader = ImageLoadingManager.getLoader(context)
        AsyncImage(
            model = imageRequest,
            imageLoader = imageLoader,
            contentDescription = "GIF",
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick(gif) },
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_error),
        )
        Box(
            modifier = Modifier
                .padding(top = 10.dp, end = 10.dp)
                .size(36.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color.LightGray)
                .align(Alignment.TopEnd)
                .clickable {
                    onDelete(gif)
                }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)

            )
        }
    }

}

