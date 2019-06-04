package com.gandan.a1xkcd.util

import kotlinx.coroutines.Dispatchers

object AppDispatchers {
    val network = Dispatchers.IO

    val main = Dispatchers.Main
}