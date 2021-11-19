package com.gandan.c1xkcd.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.gandan.c1xkcd.MainViewModel
import com.gandan.c1xkcd.entity.Strip
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun ComicStrip(viewModel: MainViewModel) {
    val state : State<Strip?> = viewModel.latest.collectAsState(initial = null)
    val strip by remember { state }

    val errorState : State<Throwable?> = viewModel.error.collectAsState(initial = null)
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
