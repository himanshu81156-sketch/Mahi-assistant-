package com.example.ui

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.MahiCyan
import com.example.ui.theme.MahiDeepNavy
import com.example.ui.theme.MahiMagenta
import com.example.ui.theme.MahiObsidian
import com.example.ui.theme.MahiOutline
import com.example.ui.theme.MahiRose
import com.example.ui.theme.MahiSurface
import com.example.ui.theme.MahiTextPrimary
import com.example.ui.theme.MahiTextSecondary
import com.example.ui.theme.MahiViolet
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoStudioTab(
    selectedVideoLabel: String?,
    onRecordCameraVideo: () -> Unit,
    onPickGalleryVideo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var recordState by remember { mutableStateOf("Idle") } // Idle, Recording, Paused
    var aiPrompt by remember { mutableStateOf("") }
    var videoStyle by remember { mutableStateOf("Cinematic") }
    var trimRange by remember { mutableStateOf(0f..15f) }
    var selectedMusic by remember { mutableStateOf("Trending Song") }
    var overlayText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Normal") }
    var selectedSpeed by remember { mutableStateOf("1x") }
    var isReelsRatio by remember { mutableStateOf(true) }
    var isGenerating by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // A) VIDEO BANAO
            SectionTitle("A) Video Banao")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        recordState = if (recordState == "Recording") "Paused" else "Recording"
                        if (recordState == "Recording") onRecordCameraVideo()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (recordState == "Recording") MahiRose else MahiCyan,
                        contentColor = MahiObsidian,
                    ),
                    modifier = Modifier.weight(1f).height(48.dp),
                ) {
                    Icon(
                        imageVector = if (recordState == "Recording") Icons.Rounded.Pause else Icons.Rounded.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(if (recordState == "Recording") "Pause" else "Start", maxLines = 1)
                }
                OutlinedButton(
                    onClick = { recordState = "Idle" },
                    border = BorderStroke(1.dp, MahiRose),
                    modifier = Modifier.weight(1f).height(48.dp),
                ) {
                    Icon(Icons.Rounded.Stop, contentDescription = null, tint = MahiRose, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("Stop", color = MahiTextPrimary, maxLines = 1)
                }
                OutlinedButton(
                    onClick = onPickGalleryVideo,
                    border = BorderStroke(1.dp, MahiMagenta),
                    modifier = Modifier.weight(1f).height(48.dp),
                ) {
                    Icon(Icons.Rounded.VideoLibrary, contentDescription = null, tint = MahiMagenta, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("Gallery", color = MahiTextPrimary, maxLines = 1)
                }
            }
            if (selectedVideoLabel != null) {
                Text("Video: $selectedVideoLabel", style = MaterialTheme.typography.labelMedium, color = MahiCyan)
            }

            // B) AI VIDEO BANAO
            SectionTitle("B) AI Video Banao")
            OutlinedTextField(
                value = aiPrompt,
                onValueChange = { aiPrompt = it },
                placeholder = { Text("Mera photo se video banao, cafe me chal raha hu…", color = MahiTextSecondary) },
                minLines = 2,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MahiMagenta,
                    unfocusedBorderColor = MahiOutline,
                    focusedContainerColor = MahiSurface,
                    unfocusedContainerColor = MahiSurface,
                    focusedTextColor = MahiTextPrimary,
                    unfocusedTextColor = MahiTextPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            ChipSelectorRow(
                label = "Video Style",
                options = listOf("Cinematic", "Slow Motion", "Fast", "Reels Style", "4K"),
                selected = videoStyle,
                accent = MahiMagenta,
                onSelect = { videoStyle = it },
            )
            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        statusMessage = null
                        delay(850L)
                        isGenerating = false
                        isPlaying = true
                        statusMessage = "Demo AI Video Ready ($videoStyle • Speed $selectedSpeed)!"
                    }
                },
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = MahiMagenta, contentColor = MahiObsidian),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp),
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = MahiObsidian, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.loading_hindi))
                } else {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("AI Video Banao")
                }
            }

            // C) VIDEO EDIT KARO
            SectionTitle("C) Video Edit Karo")
            Text(
                text = "Trim Video: ${trimRange.start.toInt()}s - ${trimRange.endInclusive.toInt()}s",
                style = MaterialTheme.typography.labelMedium,
                color = MahiTextSecondary,
            )
            RangeSlider(
                value = trimRange,
                onValueChange = { trimRange = it },
                valueRange = 0f..30f,
                colors = SliderDefaults.colors(thumbColor = MahiCyan, activeTrackColor = MahiCyan),
            )

            ChipSelectorRow(
                label = "Music Add Karo (Voice Mix)",
                options = listOf("Trending Song", "Sad", "Happy", "No Music"),
                selected = selectedMusic,
                accent = MahiCyan,
                onSelect = { selectedMusic = it },
            )
            OutlinedTextField(
                value = overlayText,
                onValueChange = { overlayText = it },
                placeholder = { Text("Video pe naam ya text likho…", color = MahiTextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MahiCyan,
                    unfocusedBorderColor = MahiOutline,
                    focusedContainerColor = MahiSurface,
                    unfocusedContainerColor = MahiSurface,
                    focusedTextColor = MahiTextPrimary,
                    unfocusedTextColor = MahiTextPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            ChipSelectorRow(
                label = "Filter",
                options = listOf("Normal", "Warm", "Cool", "B&W"),
                selected = selectedFilter,
                accent = MahiViolet,
                onSelect = { selectedFilter = it },
            )
            ChipSelectorRow(
                label = "Speed",
                options = listOf("0.5x", "1x", "2x"),
                selected = selectedSpeed,
                accent = MahiMagenta,
                onSelect = { selectedSpeed = it },
            )

            // VIDEO MOTION PREVIEW
            val speedDuration = when (selectedSpeed) {
                "0.5x" -> 5000
                "2x" -> 1400
                else -> 2800
            }
            val phase = if (isPlaying) {
                val transition = rememberInfiniteTransition(label = "videoReel")
                val p by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = (2 * PI).toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(speedDuration, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    ),
                    label = "videoPhase",
                )
                p
            } else {
                0.5f
            }

            Surface(
                color = MahiSurface.copy(alpha = 0.92f),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(MahiMagenta.copy(alpha = 0.6f), MahiCyan.copy(alpha = 0.5f)),
                    ),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(if (isReelsRatio) 9f / 12f else 16f / 9f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MahiDeepNavy),
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(MahiDeepNavy, MahiMagenta.copy(alpha = 0.35f), MahiCyan.copy(alpha = 0.25f)),
                                ),
                            )
                            val cx = size.width * (0.5f + 0.2f * sin(phase))
                            val cy = size.height * 0.5f
                            drawCircle(
                                color = MahiCyan.copy(alpha = 0.35f),
                                radius = size.minDimension * 0.24f,
                                center = Offset(cx, cy),
                            )
                        }

                        if (overlayText.isNotBlank()) {
                            Text(
                                text = overlayText,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }

                        Button(
                            onClick = { isPlaying = !isPlaying },
                            colors = ButtonDefaults.buttonColors(containerColor = MahiObsidian.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp),
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = MahiCyan,
                            )
                        }
                    }
                    statusMessage?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, style = MaterialTheme.typography.labelMedium, color = MahiCyan)
                    }
                }
            }

            // D) VIDEO SAVE / SHARE
            SectionTitle("D) Video Save / Share")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = {
                        isReelsRatio = false
                        statusMessage = "Video Gallery me save ho gaya (Demo Preview)!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MahiCyan, contentColor = MahiObsidian),
                    modifier = Modifier.weight(1f).height(48.dp),
                ) {
                    Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("Save Gallery", maxLines = 1)
                }
                Button(
                    onClick = {
                        isReelsRatio = true
                        statusMessage = "Reels / Shorts [9:16] format set ho gaya!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MahiMagenta, contentColor = MahiObsidian),
                    modifier = Modifier.weight(1f).height(48.dp),
                ) {
                    Text("Reels [9:16]", maxLines = 1)
                }
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Mera AI Assistant se banaya video ($videoStyle, Music: $selectedMusic)!",
                            )
                        }
                        runCatching { context.startActivity(Intent.createChooser(shareIntent, "Share Video")) }
                    },
                    border = BorderStroke(1.dp, MahiCyan),
                    modifier = Modifier.weight(1f).height(48.dp),
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null, tint = MahiCyan, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text("Share", color = MahiTextPrimary, maxLines = 1)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
