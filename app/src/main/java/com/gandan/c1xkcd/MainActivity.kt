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
import com.gandan.c1xkcd.io.service.XkcdService
import com.gandan.c1xkcd.ui.theme.C1XkcdTheme
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

@ExperimentalSerializationApi
class MainActivity : ComponentActivity() {

    private val service: XkcdService = Retrofit.Builder()
        .baseUrl("https://www.xkcd.com")
        .addConverterFactory(Json {
            ignoreUnknownKeys = true
        }.asConverterFactory(MediaType.get("application/json")))
        .build()
        .create(XkcdService::class.java)

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get()
        viewModel.service = service
        setContent {
            C1XkcdTheme {
                Screen(viewModel)
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.fetchLatest()
        }
    }
}

class MainViewModel : ViewModel() {
    var service : XkcdService? = null
    private val _latest: MutableLiveData<Strip> = MutableLiveData()
    val latest: LiveData<Strip> = _latest

    suspend fun fetchLatest() {
        val result = withContext(Dispatchers.IO) {
            service!!.latest()
        }
        _latest.value = result
    }


}

@Composable
fun Screen(viewModel: MainViewModel) {

    val strip = viewModel.latest.observeAsState()
    val text by remember { strip }

    Scaffold(topBar = { TopAppBar() }) {
        Column {
            text?.let {
                Greeting(it.title)
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
fun Greeting(text: String) {
    Text("Hi, 1xkcd lover $text!")
}

@Composable
fun TopAppBar() {
    androidx.compose.material.TopAppBar(title = { Text(text = "C1Xkcd") })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    C1XkcdTheme {
        Screen(MainViewModel())
    }
}
