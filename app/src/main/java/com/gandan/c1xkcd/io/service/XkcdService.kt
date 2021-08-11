package com.gandan.c1xkcd.io.service

import com.gandan.c1xkcd.entity.Strip
import retrofit2.http.GET

interface XkcdService {

    @GET("/info.0.json")
    suspend fun latest() : Strip

}