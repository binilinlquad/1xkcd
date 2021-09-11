package com.gandan.c1xkcd.ui.screen

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import com.gandan.c1xkcd.MainViewModel

@Composable
fun ScreenGlobalMessage(
    snackbarHostState: SnackbarHostState,
    viewModel: MainViewModel
) {
    SnackbarHost(hostState = snackbarHostState)

    // composing error message
    val errorState = viewModel.error.observeAsState()
    val error by remember { errorState }

    if (error == true) {
        LaunchedEffect(snackbarHostState) {
         snackbarHostState.showSnackbar("Free error app is statistically not exist", duration = SnackbarDuration.Short)
        }
    }
}

@Composable
fun ScreenGlobalMessage2(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: MainViewModel
) {
    // composing error message
    val errorState = viewModel.error.observeAsState()
    val error by remember { errorState }

    if (error == true) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar("Free error app is statistically not exist", duration = SnackbarDuration.Short)
        }
    }
}
