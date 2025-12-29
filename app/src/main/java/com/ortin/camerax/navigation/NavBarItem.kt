package com.ortin.camerax.navigation

import com.ortin.camerax.R

sealed class NavigationBarItem(
    val title: String,
    val icon: Int,
    val route: String
) {
    data object Photo : NavigationBarItem(
        title = "Фото",
        icon = R.drawable.ic_camera,
        route = ScreenRoutes.PHOTO_SCREEN
    )

    data object Video : NavigationBarItem(
        title = "Видео",
        icon = R.drawable.ic_video,
        route = ScreenRoutes.VIDEO_SCREEN
    )

    data object Gallery : NavigationBarItem(
        title = "Галерея",
        icon = R.drawable.ic_gallery,
        route = ScreenRoutes.GALLERY_SCREEN
    )
}
