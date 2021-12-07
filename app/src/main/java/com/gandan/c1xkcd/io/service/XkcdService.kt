package com.gandan.c1xkcd.io.service

import com.gandan.c1xkcd.entity.Strip
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdService {

    @GET("/info.0.json")
    suspend fun latest() : Strip

    @GET("/{page}/info.0.json")
    suspend fun page(@Path("page") pos: Int) : Strip


}