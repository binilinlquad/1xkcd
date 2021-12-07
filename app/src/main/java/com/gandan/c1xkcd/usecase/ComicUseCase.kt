package com.gandan.c1xkcd.usecase

import com.gandan.c1xkcd.io.Runtime
import com.gandan.c1xkcd.io.bg

suspend fun Runtime.latestStrip() = bg { service.latest() }

suspend fun Runtime.stripAt(page: Int) = bg { service.page(page) }