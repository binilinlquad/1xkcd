package com.gandan.c1xkcd.ui.screen

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow

@Composable
fun ScreenGlobalMessage(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    error: Flow<Throwable?>
) {
    // composing error message
    val errorState = error.collectAsState(initial = null)
    val e by remember { errorState }

    if (e != null) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar("Free error app is statistically not exist", duration = SnackbarDuration.Short)
        }
    }
}
