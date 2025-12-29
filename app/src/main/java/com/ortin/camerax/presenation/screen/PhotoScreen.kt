package com.ortin.camerax.presenation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ortin.camerax.presenation.component.CameraControls
import com.ortin.camerax.presenation.component.CameraPreview
import com.ortin.camerax.presenation.viewModel.PhotoScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoScreen() {
    val context = LocalContext.current
    val viewModel: PhotoScreenViewModel = koinViewModel()

    val coroutineScope = rememberCoroutineScope()

    val hasPermission = remember { mutableStateOf(false) }
    val isFlashVisible = remember { mutableStateOf(false) }

    val flashAlpha by animateFloatAsState(
        targetValue = if (isFlashVisible.value) 0.7f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasPermission.value = isGranted

            if (!isGranted) {
                Toast.makeText(
                    context,
                    "The camera permission is necessary",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    LaunchedEffect(Unit) {
        hasPermission.value =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission.value) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(viewModel.currentToastTextToShow.value) {
        if (!viewModel.currentToastTextToShow.value.isNullOrEmpty()) {
            Toast.makeText(
                context,
                viewModel.currentToastTextToShow.value,
                Toast.LENGTH_SHORT
            ).show()

            viewModel.currentToastTextToShow.value = ""
        }
    }

    if (!hasPermission.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Разрешите доступ к камере")
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(viewModel)

            CameraControls(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                onTakePhoto = {
                    isFlashVisible.value = true
                    viewModel.takePhoto(context)

                    coroutineScope.launch {
                        delay(400)
                        isFlashVisible.value = false
                    }
                },
                onChangeCamera = { viewModel.changeCamera() }
            )

            if (isFlashVisible.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White.copy(alpha = flashAlpha))
                )
            }
        }
    }
}
