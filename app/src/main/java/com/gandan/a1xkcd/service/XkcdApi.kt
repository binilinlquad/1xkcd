package com.gandan.a1xkcd.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface XkcdApi {

    @GET("info.0.json")
    fun latestStrip(): Call<Page>

    @GET("{pos}/info.0.json")
    fun at(@Path("pos") pos: Int): Call<Page>

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

fun createXkcdApi(baseUrl: String): XkcdApi {
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XkcdApi::class.java)
}