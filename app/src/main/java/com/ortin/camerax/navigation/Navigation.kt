package com.ortin.camerax.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ortin.camerax.presenation.component.CustomBottomBar
import com.ortin.camerax.presenation.screen.FullViewerPhotoScreen
import com.ortin.camerax.presenation.screen.FullViewerVideoScreen
import com.ortin.camerax.presenation.screen.GalleryScreen
import com.ortin.camerax.presenation.screen.PhotoScreen
import com.ortin.camerax.presenation.screen.VideoScreen
import com.ortin.camerax.presenation.ui.CameraXTheme
import com.ortin.camerax.presenation.viewModel.GalleryScreenViewModel
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder

@Composable
fun MainNanGraph() {
    val context = LocalContext.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val viewModel: GalleryScreenViewModel = koinViewModel()

    val hideNavBarPrefix = listOf(
        ScreenRoutes.PHOTO_FULL_SCREEN,
        ScreenRoutes.VIDEO_FULL_SCREEN
    )

    CameraXTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentRoute != null && hideNavBarPrefix.none { currentRoute.contains(it) }) {
                    CustomBottomBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ScreenRoutes.PHOTO_SCREEN,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.White),
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) }
            ) {
                composable(ScreenRoutes.PHOTO_SCREEN) {
                    PhotoScreen()
                }

                composable(ScreenRoutes.VIDEO_SCREEN) {
                    VideoScreen()
                }

                composable(ScreenRoutes.GALLERY_SCREEN) {
                    GalleryScreen(navController)
                }

                composable("${ScreenRoutes.PHOTO_FULL_SCREEN}/{fileUri}") { navBackStackEntry ->
                    val encodedUri = navBackStackEntry.arguments?.getString("fileUri")
                    val uri = encodedUri?.let { URLDecoder.decode(it, "UTF-8") }?.toUri()

                    FullViewerPhotoScreen(
                        fileUri = uri,
                        onBack = { navController.popBackStack() },
                        onDelete = {
                            viewModel.deleteMedia(context, uri)
                            navController.popBackStack()
                        }
                    )
                }

                composable("${ScreenRoutes.VIDEO_FULL_SCREEN}/{fileUri}") { navBackStackEntry ->
                    val encodedUri = navBackStackEntry.arguments?.getString("fileUri")
                    val uri = encodedUri?.let { URLDecoder.decode(it, "UTF-8") }?.toUri()

                    FullViewerVideoScreen(
                        videoUri = uri,
                        onBack = { navController.popBackStack() },
                        onDelete = {
                            viewModel.deleteMedia(context, uri)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
