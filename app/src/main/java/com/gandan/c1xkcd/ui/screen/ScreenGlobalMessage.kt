package com.gandan.c1xkcd.ui.screen

import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*

@Composable
fun ScreenGlobalMessage(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    error: State<Throwable?>
) {
    // composing error message
    val e by remember { error }

    if (e != null) {
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar("Free error app is statistically not exist", duration = SnackbarDuration.Short)
        }
    }
}
