package com.ortin.camerax.presenation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ortin.camerax.presenation.component.VideoControls
import com.ortin.camerax.presenation.viewModel.VideoScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun VideoScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: VideoScreenViewModel = koinViewModel()

    val previewView = remember { PreviewView(context) }

    val hasPermission = remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            hasPermission.value = isGranted

            if (!isGranted) {
                Toast.makeText(
                    context,
                    "The microphone permission is necessary",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    LaunchedEffect(Unit) {
        hasPermission.value =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission.value) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
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
            Text("Разрешите доступ к камере и микрофону")
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LaunchedEffect(Unit) {
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

            VideoControls(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                isRecording = viewModel.isRecording.value,
                duration = viewModel.recordingDuration.longValue,
                onRecordClick = { viewModel.toggleRecording(context) },
                onChangeCamera = {
                    viewModel.changeCamera(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        previewView = previewView
                    )
                }
            )
        }
    }
}
