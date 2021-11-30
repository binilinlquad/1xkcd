package com.gandan.c1xkcd.ui.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gandan.c1xkcd.MainViewModel
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun Screen(error: Flow<Throwable?>, loadingState: State<Boolean>, content: @Composable ()->Unit) {
    val loading by remember { loadingState }

    val scaffoldState = rememberScaffoldState()

    ScreenGlobalMessage(scaffoldState = scaffoldState, error =  error)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar() },
    ) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
        ) {
            if (loading) {
                InfiniteCircularProgressAnimation()
            } else {
                content()
            }
        }
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
    val loadingState : State<Boolean> = viewModel.loading.collectAsState(initial = false)
    val comicStrip : State<Strip?> = viewModel.latest.collectAsState(initial = null)
    val errorState : State<Throwable?> = viewModel.error.collectAsState(initial = null)

    C1XkcdTheme {
        Screen(viewModel.error, loadingState) { ComicStrip(comicStrip, errorState) }
    }
}
