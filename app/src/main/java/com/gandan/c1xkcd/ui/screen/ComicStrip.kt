package com.gandan.c1xkcd.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
            ComicImage(it.img, it.alt)
        }
    }
}


@Composable
fun Title(text: String) {
    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 24.sp)
}

@Composable
fun AltText(text: String) {
    Text("Alt $text!")
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ComicImage(url: String, altText: String? = null) {
    Image(
        painter = rememberImagePainter(
            data = url,
            onExecute = ImagePainter.ExecuteCallback { _, _ -> true },
            builder = {
                crossfade(true)
            }
        ),
        contentDescription = "Comic tell about $altText",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier.fillMaxWidth()
    )
}
