package com.gandan.c1xkcd.io

import com.gandan.c1xkcd.io.service.XkcdService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


data class Env(
    val bgDispatcher: CoroutineDispatcher,
)

class Runtime(
    val env: Env,
    val service: XkcdService
)

suspend fun <R> Runtime.bg(lambda: suspend () -> R) = withContext(env.bgDispatcher) { lambda() }

