package com.gandan.c1xkcd.io.service

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

private val content_type = MediaType.get("application/json")
private val json = Json { ignoreUnknownKeys = true }

@ExperimentalSerializationApi
fun retrofit(baseUrl: String) : Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(json.asConverterFactory(content_type))
    .build()