package com.ortin.camerax.presenation.component

import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ortin.camerax.presenation.viewModel.PhotoScreenViewModel

@Composable
fun CameraPreview(
    viewModel: PhotoScreenViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val previewView = remember { PreviewView(context) }

    var zoom by remember { mutableStateOf(0f) }
    val camera = remember { viewModel.camera }

    LaunchedEffect(viewModel.cameraSelector.value) {
        viewModel.bindCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView
        )
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(camera) {
                detectTapGestures { offset ->
                    camera?.let {
                        viewModel.focusOnPoint(it, offset.x, offset.y, previewView)
                    }
                }
            }
            .pointerInput(camera) {
                detectTransformGestures { _, _, zoomChange, _ ->
                    camera?.let {
                        val newScale = zoom - (1f - zoomChange)

                        zoom = newScale.coerceIn(0f, 1f)

                        it.cameraControl.setLinearZoom(zoom)
                    }
                }
            },
        factory = { previewView }
    )
}
