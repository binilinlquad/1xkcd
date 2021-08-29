package com.gandan.c1xkcd

import com.gandan.c1xkcd.io.Env
import com.gandan.c1xkcd.io.Runtime
import com.gandan.c1xkcd.io.service.XkcdService
import com.gandan.c1xkcd.io.service.retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi

private val env = Env(Dispatchers.IO)

@ExperimentalSerializationApi
val RUNTIME = Runtime(env, retrofit("https://www.xkcd.com").create(XkcdService::class.java))
