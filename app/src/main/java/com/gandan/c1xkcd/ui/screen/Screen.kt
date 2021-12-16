package com.gandan.c1xkcd.ui.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.gandan.c1xkcd.MainViewModel
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun Screen(error: Flow<Throwable?>, content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()

    ScreenGlobalMessage(scaffoldState = scaffoldState, error = error)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar() },
    ) {
        content()
    }
}

@Composable
fun InfiniteCircularProgressAnimation() {
    val infiniteProgress by infiniteProgressAnimation()

    CircularProgressIndicator(progress = infiniteProgress)
}

@Composable
fun infiniteProgressAnimation(): State<Float> {
    val progressState = rememberInfiniteTransition()
    return progressState.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750)
        )
    )
}

@Composable
fun TopAppBar() {
    androidx.compose.material.TopAppBar(title = { Text(text = "C1Xkcd") })
}

@ExperimentalSerializationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = MainViewModel()
    val error: Flow<Throwable?> = viewModel.error
    val comicStrip: Flow<Strip?> =
        flowOf(
            Strip(
                1,
                "Look at this Awesome comic!!!",
                "https://picsum.photos/200/300",
                "It is so awesome!"
            )
        )

    C1XkcdTheme {
        Screen(error) { ComicStrip(comicStrip) }
    }
}
