package com.ortin.camerax.presenation.viewModel

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoScreenViewModel : ViewModel() {

    private var camera: Camera? = null

    private var videoCapture: VideoCapture<Recorder>? = null

    private var recording: Recording? = null

    val cameraSelector = mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)

    val isRecording = mutableStateOf(false)

    val recordingDuration = mutableLongStateOf(0L)

    val currentToastTextToShow = mutableStateOf<String?>(null)

    private var timerJob: Job? = null

    private val recorder = Recorder.Builder()
        .setQualitySelector(QualitySelector.from(Quality.HD))
        .build()

    fun bindCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            videoCapture = VideoCapture.withOutput(recorder)

            try {
                cameraProvider.unbindAll()

                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector.value,
                    preview,
                    videoCapture
                )

                if (isRecording.value) {
                    recording?.resume()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Bind failed", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun toggleRecording(context: Context) {
        val videoCapture = videoCapture ?: return

        if (isRecording.value) {
            stopRecording()
            return
        }

        val fileName = "VID_${System.currentTimeMillis()}.mp4"

        val now = System.currentTimeMillis()

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX")

            put(MediaStore.Video.Media.DATE_TAKEN, now)
            put(MediaStore.Video.Media.DATE_ADDED, now / 1000)
            put(MediaStore.Video.Media.DATE_MODIFIED, now / 1000)
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .asPersistentRecording()
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        isRecording.value = true
                        startTimer()
                    }

                    is VideoRecordEvent.Finalize -> {
                        stopTimer()
                        isRecording.value = false
                        recording = null

                        currentToastTextToShow.value =
                            if (event.hasError()) {
                                "Ошибка записи видео"
                            } else {
                                "Видео сохранено в галерею"
                            }
                    }
                }
            }
    }

    fun changeCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        if (isRecording.value) {
            recording?.pause()
        }

        cameraSelector.value = if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        bindCamera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            previewView = previewView
        )
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }

    private fun startTimer() {
        recordingDuration.longValue = 0
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                recordingDuration.longValue++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    companion object {
        private const val TAG = "VideoScreenViewModel"
    }
}
