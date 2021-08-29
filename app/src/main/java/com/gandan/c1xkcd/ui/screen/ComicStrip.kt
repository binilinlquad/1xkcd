package com.gandan.c1xkcd.ui.screen

import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gandan.c1xkcd.MainViewModel

@Composable
fun ComicStrip(viewModel: MainViewModel) {

    val state = viewModel.latest.observeAsState()
    val strip by remember { state }

    val errorState = viewModel.error.observeAsState()
    val error by remember { errorState }


    if (error == true) {
        Title("Error happened.")
    } else {
        strip?.let {
            Title(it.title)
            AltText(it.alt)
            Image(it.img)
        }
    }
    Button(
        onClick = { },
        modifier = Modifier.wrapContentWidth()
    ) {
        Text(text = "Click Me!")
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

@Composable
fun Image(text: String) {
    Text("Img $text!")
}
