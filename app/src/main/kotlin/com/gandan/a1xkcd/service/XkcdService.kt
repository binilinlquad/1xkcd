package com.gandan.a1xkcd.service

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdService {

    @GET("info.0.json")
    suspend fun latestPage(): Page

    @GET("{pos}/info.0.json")
    suspend fun at(@Path("pos") pos: Int): Page

}

data class Page(
        @SerializedName("month")
        val month: String,
        @SerializedName("num")
        val num: Int,
        @SerializedName("link")
        val link: String,
        @SerializedName("year")
        val year: String,
        @SerializedName("news")
        val news: String,
        @SerializedName("safe_title")
        val safeTitle: String,
        @SerializedName("transcript")
        val transcript: String,
        @SerializedName("alt")
        val alt: String,
        @SerializedName("img")
        val img: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("day")
        val day: String
)

fun createXkcdService(webClient: OkHttpClient, baseUrl: String): XkcdService {
    return Retrofit.Builder()
            .client(webClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XkcdService::class.java)
}
