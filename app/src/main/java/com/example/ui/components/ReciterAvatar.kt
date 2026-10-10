package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.data.model.Reciter

@Composable
fun ReciterAvatar(
    reciter: Reciter,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(modifier = modifier.clip(CircleShape)) {
        if (reciter.localDrawableRes != null) {
            Image(
                painter = painterResource(id = reciter.localDrawableRes),
                contentDescription = reciter.name,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = reciter.avatarUrl,
                contentDescription = reciter.name,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
