package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.*
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.io.Env
import com.gandan.c1xkcd.io.Runtime
import com.gandan.c1xkcd.io.service.XkcdService
import com.gandan.c1xkcd.io.service.retrofit
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import com.gandan.c1xkcd.usecase.latestStrip
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi

val env = Env(Dispatchers.IO)
@ExperimentalSerializationApi
val runtime = Runtime(env, retrofit("https://www.xkcd.com").create(XkcdService::class.java))

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        setContent {
            C1XkcdTheme {
                Screen(viewModel)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.refresh()
        }
    }
}

@ExperimentalSerializationApi
class MainViewModel : ViewModel() {
    private val _latest: MutableLiveData<Strip> = MutableLiveData()
    val latest: LiveData<Strip> = _latest

    suspend fun refresh() {
        _latest.value = runtime.latestStrip()
    }
}
@ExperimentalSerializationApi
@Composable
fun Screen(viewModel: MainViewModel) {

    val state = viewModel.latest.observeAsState()
    val strip by remember { state }

    Scaffold(topBar = { TopAppBar() }) {
        Column {
            strip?.let {
                StripTitle(it.title)
                StripAlt(it.alt)
                StripImg(it.img)
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
        Screen(MainViewModel())
    }
}
