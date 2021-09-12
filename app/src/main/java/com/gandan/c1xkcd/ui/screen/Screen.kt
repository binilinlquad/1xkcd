package com.gandan.c1xkcd.ui.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gandan.c1xkcd.MainViewModel
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun Screen(viewModel: MainViewModel) {
    val loadingState = viewModel.loading.observeAsState()
    val loading by remember { loadingState }

    val scaffoldState = rememberScaffoldState()
    ScreenGlobalMessage2(scaffoldState = scaffoldState, viewModel = viewModel)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar() },
    ) {
        Column {
            if (loading == true) {
                InfiniteCircularProgressAnimation()
            } else {
                ComicStrip(viewModel = viewModel)
            }

            Button(
                onClick = { viewModel._error.value = viewModel._error.value?.not() ?: true },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(text = "Toggle error snackbar!")
            }

            Button(
                onClick = { viewModel._loading.value = viewModel._loading.value?.not() ?: true },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(text = "Toggle Loading!")
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
fun infiniteProgressAnimation() : State<Float> {
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
