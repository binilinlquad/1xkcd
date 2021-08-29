package com.gandan.c1xkcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
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
import androidx.lifecycle.*
import arrow.core.Either
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import com.gandan.c1xkcd.usecase.latestStrip
import kotlinx.serialization.ExperimentalSerializationApi

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
    private val _error: MutableLiveData<Boolean> = MutableLiveData()
    val error: LiveData<Boolean> = _error
    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    suspend fun refresh() {
        _loading.value = true
        when(val result  = Either.catch { RUNTIME.latestStrip() }) {
            is Either.Right -> {
                _error.value = false
                _latest.value = result.value
            }
            else -> _error.value = true
        }
        _loading.value = false
    }
}
@ExperimentalSerializationApi
@Composable
fun Screen(viewModel: MainViewModel) {

    val state = viewModel.latest.observeAsState()
    val strip by remember { state }

    val errorState = viewModel.error.observeAsState()
    val error by remember { errorState }

    val loadingState = viewModel.loading.observeAsState()
    val loading by remember { loadingState }
    val progressState = rememberInfiniteTransition()
    val progress by progressState.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 750)
    ))


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
        Screen(MainViewModel())
    }
}
