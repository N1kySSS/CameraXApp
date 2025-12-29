package com.ortin.camerax.presenation.component

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ortin.camerax.presenation.viewModel.VideoScreenViewModel

@Composable
fun VideoCameraPreview(viewModel: VideoScreenViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }

    LaunchedEffect(viewModel.cameraSelector.value) {
        viewModel.bindCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView
        )
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}
