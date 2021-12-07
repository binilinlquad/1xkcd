package com.gandan.c1xkcd.entity

import kotlinx.serialization.Serializable

@Serializable
data class Strip(val num: Int, val title: String, val img: String, val alt: String)
