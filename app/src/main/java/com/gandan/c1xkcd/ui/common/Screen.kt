package com.gandan.c1xkcd.ui.common

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun Screen(error: State<Throwable?>, content: @Composable () -> Unit) {
    val scaffoldState = rememberScaffoldState()

    SnackbarErrorMessage(scaffoldState = scaffoldState, error = error)

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
fun InfiniteHorizontalProgressAnimation() {
    val infiniteProgress by infiniteProgressAnimation()

    LinearProgressIndicator(
        modifier = Modifier.fillMaxSize(),
        progress = infiniteProgress)
}


@Composable
fun infiniteProgressAnimation(): State<Float> {
    val progressState = rememberInfiniteTransition()
    return progressState.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 750))
    )
}

@Composable
fun TopAppBar() {
    androidx.compose.material.TopAppBar(title = { Text(text = "C1Xkcd") })
}

@SuppressLint("UnrememberedMutableState")
@ExperimentalSerializationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val error: State<Throwable?> = mutableStateOf(null)
    val comicStrip: State<Strip?> = mutableStateOf(
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
