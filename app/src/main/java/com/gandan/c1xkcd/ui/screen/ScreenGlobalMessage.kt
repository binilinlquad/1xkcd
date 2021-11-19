package com.gandan.c1xkcd.ui.screen

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import com.gandan.c1xkcd.MainViewModel

@Composable
fun ScreenGlobalMessage(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: MainViewModel
) {
    // composing error message
    val errorState = viewModel.error.collectAsState(initial = null)
    val error by remember { errorState }

    if (error != null) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar("Free error app is statistically not exist", duration = SnackbarDuration.Short)
        }
    }
}
