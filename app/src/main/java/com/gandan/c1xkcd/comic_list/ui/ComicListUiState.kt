package com.gandan.c1xkcd.comic_list.ui

import com.gandan.c1xkcd.entity.Strip

enum class AppTheme {
    LIGHT,
    DARK,
    AUTO
}

data class ComicListUiStat(
    val theme: AppTheme,
    val comics: List<Strip>,
    val error: Throwable?
)
