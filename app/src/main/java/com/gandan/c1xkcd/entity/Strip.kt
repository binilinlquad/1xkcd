package com.gandan.c1xkcd.entity

import kotlinx.serialization.Serializable

@Serializable
data class Strip(val title: String, val img: String, val alt: String)
