package com.ortin.camerax.presenation.viewModel

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.ortin.camerax.model.MediaItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryScreenViewModel : ViewModel() {

    val photos = mutableStateListOf<MediaItem>()

    val videos = mutableStateListOf<MediaItem>()

    fun loadMedia(context: Context) {
        photos.clear()
        videos.clear()

        loadPhotos(context)
        loadVideos(context)
    }

    fun deleteMedia(context: Context, uri: Uri?) {
        uri?.let {
            try {
                context.contentResolver.delete(uri, null, null)
            } catch (e: Exception) {
                Log.e(TAG, "Произошла ошибка при удалении файла: $uri", e)
            }
        } ?: Log.e(TAG, "Uri равен null")
    }

    private fun loadPhotos(context: Context) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED
        )

        val selection =
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"

        val selectionArgs = arrayOf(
            "%Pictures/CameraX%"
        )

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Images.Media._ID)
            val dateTakenCol = it.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
            val dateAddedCol = it.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)

            if (idColumn == -1) {
                Log.e(TAG, "Get photo failed")
                return@use
            }

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)

                val dateTaken =
                    if (dateTakenCol != -1) it.getLong(dateTakenCol) else 0L

                val dateAdded =
                    if (dateAddedCol != -1) it.getLong(dateAddedCol) * 1000 else 0L

                val date =
                    if (dateTaken > 0L) dateTaken else if (dateAdded > 0L) dateAdded else System.currentTimeMillis()

                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                photos.add(MediaItem(id, uri, formatDate(date)))
            }
        }
    }

    private fun loadVideos(context: Context) {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATE_ADDED
        )

        val selection =
            "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"

        val selectionArgs = arrayOf(
            "%Movies/CameraX%"
        )

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Video.Media._ID)
            val dateTakenCol = it.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)
            val dateAddedCol = it.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)

            if (idColumn == -1) {
                Log.e(TAG, "Get video failed")
                return@use
            }

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)

                val dateTaken =
                    if (dateTakenCol != -1) it.getLong(dateTakenCol) else 0L

                val dateAdded =
                    if (dateAddedCol != -1) it.getLong(dateAddedCol) * 1000 else 0L

                val date =
                    if (dateTaken > 0L) dateTaken else if (dateAdded > 0L) dateAdded else System.currentTimeMillis()

                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                videos.add(MediaItem(id, uri, formatDate(date)))
            }
        }
    }

    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        return formatter.format(Date(millis))
    }

    companion object {
        private const val TAG = "PhotoScreenViewModel"
    }
}
