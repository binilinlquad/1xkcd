package com.gandan.c1xkcd.ui.screen

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
fun MainScreen(viewModel: MainViewModel) {

    val state = viewModel.latest.observeAsState()
    val strip by remember { state }

    val errorState = viewModel.error.observeAsState()
    val error by remember { errorState }

    val loadingState = viewModel.loading.observeAsState()
    val loading by remember { loadingState }
    val progressState = rememberInfiniteTransition()
    val progress by progressState.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 750)
    )
    )


    Scaffold(topBar = { TopAppBar() }) {
        Column {
            if (loading == true) {
                CircularProgressIndicator(progress = progress)
            }
            if (error == true) {
                StripTitle("Error happened.")
            } else {
                strip?.let {
                    StripTitle(it.title)
                    StripAlt(it.alt)
                    StripImg(it.img)
                }
            }
            Button(
                onClick = { },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(text = "Click Me!")
            }
        }
    }
}

@Composable
fun StripTitle(text: String) {
    Text("Title $text!")
}

@Composable
fun StripAlt(text: String) {
    Text("Alt $text!")
}

@Composable
fun StripImg(text: String) {
    Text("Img $text!")
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
        MainScreen(MainViewModel())
    }
}
