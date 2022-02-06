package com.gandan.c1xkcd.comic_list.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gandan.c1xkcd.entity.Strip
import com.gandan.c1xkcd.ui.common.AltText
import com.gandan.c1xkcd.ui.common.ComicImage
import com.gandan.c1xkcd.ui.common.InfiniteHorizontalProgressAnimation
import com.gandan.c1xkcd.ui.common.Title

@Composable
internal fun ComicCard(comic: Strip?) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 8.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (comic != null) {
                Title(comic.title)
                Spacer(modifier = Modifier.size(4.dp))
                ComicImage(comic.img, comic.alt)
                Spacer(modifier = Modifier.size(8.dp))
                AltText(comic.alt)
            } else {
                InfiniteHorizontalProgressAnimation()
            }

        }
    }
}
