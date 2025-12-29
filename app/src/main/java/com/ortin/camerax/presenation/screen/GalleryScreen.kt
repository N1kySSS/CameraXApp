package com.ortin.camerax.presenation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import com.ortin.camerax.navigation.ScreenRoutes
import com.ortin.camerax.presenation.utils.clickableWithoutIndication
import com.ortin.camerax.presenation.viewModel.GalleryScreenViewModel
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: GalleryScreenViewModel = koinViewModel()

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    LaunchedEffect(Unit) {
        viewModel.loadMedia(context)
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Фото",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (viewModel.photos.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Фотографий нет",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(viewModel.photos) {
                Box {
                    AsyncImage(
                        model = it.uri,
                        contentDescription = "photo",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .clickableWithoutIndication {
                                val encodedUri = URLEncoder.encode(it.uri.toString(), "UTF-8")
                                navController.navigate("${ScreenRoutes.PHOTO_FULL_SCREEN}/$encodedUri")
                            },
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = it.date,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Видео",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (viewModel.videos.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Видео нет",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(viewModel.videos) {
                Box {
                    AsyncImage(
                        model = it.uri,
                        imageLoader = imageLoader,
                        contentDescription = "video",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .clickableWithoutIndication {
                                val encodedUri = URLEncoder.encode(it.uri.toString(), "UTF-8")
                                navController.navigate("${ScreenRoutes.VIDEO_FULL_SCREEN}/$encodedUri")
                            },
                        contentScale = ContentScale.Crop
                    )

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "ic_video",
                        tint = Color.Blue,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )

                    Text(
                        text = it.date,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
