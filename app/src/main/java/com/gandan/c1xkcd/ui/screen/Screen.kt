package com.gandan.c1xkcd.ui.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.gandan.c1xkcd.MainViewModel
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun Screen(viewModel: MainViewModel) {
    val loadingState = viewModel.loading.observeAsState()
    val loading by remember { loadingState }
    val infiniteProgress by InfiniteProgress()

    // Show snackbar version 2:
    val scaffoldState = rememberScaffoldState()
    ScreenGlobalMessage2(scaffoldState = scaffoldState, viewModel = viewModel)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar() },
// Show snackbar version 1: override snackbarhost composition function
//        snackbarHost = {
//            ScreenGlobalMessage(snackbarHostState = it, viewModel = viewModel)
//        }
    ) {

        Column {
            if (loading == true) {
                CircularProgressIndicator(progress = infiniteProgress)
            } else {
                ComicStrip(viewModel = viewModel)
            }


        }

    }

}

@Composable
fun InfiniteProgress() : State<Float> {
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
    C1XkcdTheme {
        Screen(MainViewModel())
    }
}
