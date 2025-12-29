package com.ortin.camerax.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val date: String
)
