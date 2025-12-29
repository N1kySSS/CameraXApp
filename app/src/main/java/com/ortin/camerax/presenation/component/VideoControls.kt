package com.ortin.camerax.presenation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ortin.camerax.R

@Composable
fun VideoControls(
    duration: Long,
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    onChangeCamera: () -> Unit,
    modifier: Modifier = Modifier,
    elementsColor: Color = Color.Blue,
    backgroundColor: Color = Color.Transparent
) {
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isRecording) {
            IconButton(onClick = onChangeCamera) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.ic_reverse_camera),
                    contentDescription = "change camera",
                    tint = elementsColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isRecording) Color.Red else Color.White,
                    if (isRecording) RoundedCornerShape(4.dp) else CircleShape
                )
                .clickable { onRecordClick() }
        )

        if (isRecording) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatTime(duration),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

private fun formatTime(seconds: Long): String = "%02d:%02d".format(seconds / 60, seconds % 60)
