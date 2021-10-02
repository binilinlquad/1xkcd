package com.gandan.c1xkcd.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.gandan.c1xkcd.MainViewModel
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun ComicStrip(viewModel: MainViewModel) {
    val state = viewModel.latest.observeAsState()
    val strip by remember { state }

    val errorState = viewModel.error.observeAsState()
    val error by remember { errorState }

    if (error != null) {
        Title(error.toString())
    } else {
        strip?.let {
            Title(it.title)
            AltText(it.alt)
            ComicImage(it.img)
        }
    }
}


@Composable
fun Title(text: String) {
    Text("Title $text!")
}

@Composable
fun AltText(text: String) {
    Text("Alt $text!")
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ComicImage(url: String) {
    Image(
        painter = rememberImagePainter(
            data = url,
            onExecute = ImagePainter.ExecuteCallback { _, _ -> true },
            builder = {
                crossfade(true)
            }
        ),
        contentDescription = null,
        modifier = Modifier.size(128.dp)
    )
}
