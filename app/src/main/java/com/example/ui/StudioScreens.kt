package com.example.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.R
import com.example.studio.AppBlueprint
import com.example.studio.AspectRatioOption
import com.example.studio.GeneratedMediaResult
import com.example.studio.MediaType
import com.example.studio.MiniAppKind
import com.example.studio.StudioUiState
import com.example.ui.theme.MahiCyan
import com.example.ui.theme.MahiDeepNavy
import com.example.ui.theme.MahiMagenta
import com.example.ui.theme.MahiObsidian
import com.example.ui.theme.MahiOutline
import com.example.ui.theme.MahiSurface
import com.example.ui.theme.MahiTextPrimary
import com.example.ui.theme.MahiTextSecondary
import com.example.ui.theme.MahiViolet
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppBuilderScreen(
    uiState: StudioUiState,
    onPromptChange: (String) -> Unit,
    onBuildApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (uiState.isDemoMode) {
                StudioDemoNoticeBanner()
            }

            Text(
                text = stringResource(R.string.app_builder_title),
                style = MaterialTheme.typography.titleLarge,
                color = MahiTextPrimary,
            )
            Text(
                text = stringResource(R.string.app_builder_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MahiTextSecondary,
            )

            OutlinedTextField(
                value = uiState.appPrompt,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.app_builder_hint),
                        color = MahiTextSecondary,
                    )
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MahiCyan,
                    unfocusedBorderColor = MahiOutline,
                    focusedContainerColor = MahiSurface,
                    unfocusedContainerColor = MahiSurface,
                    focusedTextColor = MahiTextPrimary,
                    unfocusedTextColor = MahiTextPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_builder_input"),
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("Fitness Tracker", "Music Player", "Expense Manager").forEach { preset ->
                    Surface(
                        color = MahiSurface,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, MahiOutline),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(50))
                            .clickable { onPromptChange(preset) },
                    ) {
                        Text(
                            text = preset,
                            style = MaterialTheme.typography.labelMedium,
                            color = MahiTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        )
                    }
                }
            }

            Button(
                onClick = onBuildApp,
                enabled = !uiState.isBuildingApp,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MahiCyan,
                    contentColor = MahiObsidian,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("build_app_button"),
            ) {
                if (uiState.isBuildingApp) {
                    CircularProgressIndicator(
                        color = MahiObsidian,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.app_builder_building),
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.app_builder_cta),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            uiState.appBlueprint?.let { blueprint ->
                var showCodeTab by remember(blueprint.appName) { mutableStateOf(false) }

                Surface(
                    color = MahiSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MahiCyan.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_blueprint_card"),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = blueprint.appName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MahiCyan,
                                )
                                Text(
                                    text = blueprint.tagline,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MahiMagenta,
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Surface(
                                color = if (!showCodeTab) MahiCyan.copy(alpha = 0.18f) else MahiDeepNavy,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (!showCodeTab) MahiCyan else MahiOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showCodeTab = false },
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Text(
                                        text = "Live App Preview",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (!showCodeTab) MahiCyan else MahiTextSecondary,
                                    )
                                }
                            }
                            Surface(
                                color = if (showCodeTab) MahiMagenta.copy(alpha = 0.18f) else MahiDeepNavy,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (showCodeTab) MahiMagenta else MahiOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showCodeTab = true },
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
                                    Text(
                                        text = "Code & Screens",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (showCodeTab) MahiMagenta else MahiTextSecondary,
                                    )
                                }
                            }
                        }

                        if (!showCodeTab) {
                            LiveMiniAppPreview(blueprint = blueprint)
                        } else {
                            Text(
                                text = "Screens",
                                style = MaterialTheme.typography.titleMedium,
                                color = MahiTextPrimary,
                            )
                            blueprint.screens.forEach { screen ->
                                Text(
                                    text = "• $screen",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MahiTextSecondary,
                                )
                            }
                            Text(
                                text = "Core Features",
                                style = MaterialTheme.typography.titleMedium,
                                color = MahiTextPrimary,
                            )
                            blueprint.features.forEach { feature ->
                                Text(
                                    text = "✓ $feature",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MahiTextSecondary,
                                )
                            }
                            Surface(
                                color = MahiDeepNavy,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MahiOutline),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Code,
                                            contentDescription = null,
                                            tint = MahiCyan,
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Text(
                                            text = "Compose Starter",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MahiCyan,
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = blueprint.sampleCode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MahiTextPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MediaStudioScreen(
    uiState: StudioUiState,
    onPromptChange: (String) -> Unit,
    onSelectMediaType: (MediaType) -> Unit,
    onSelectAspectRatio: (AspectRatioOption) -> Unit,
    onToggleVideoPlayback: () -> Unit,
    onPickReferencePhoto: () -> Unit,
    onClearReferencePhoto: () -> Unit,
    onGenerateMedia: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (uiState.isDemoMode) {
                StudioDemoNoticeBanner()
            }

            Text(
                text = stringResource(R.string.media_studio_title),
                style = MaterialTheme.typography.titleLarge,
                color = MahiTextPrimary,
            )
            Text(
                text = stringResource(R.string.media_studio_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MahiTextSecondary,
            )

            // Photo / Video Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MediaTypePill(
                    label = stringResource(R.string.media_photo),
                    icon = Icons.Rounded.Image,
                    selected = uiState.selectedMediaType == MediaType.PHOTO,
                    accentColor = MahiCyan,
                    testTag = "media_type_photo",
                    onClick = { onSelectMediaType(MediaType.PHOTO) },
                    modifier = Modifier.weight(1f),
                )
                MediaTypePill(
                    label = stringResource(R.string.media_video),
                    icon = Icons.Rounded.Videocam,
                    selected = uiState.selectedMediaType == MediaType.VIDEO,
                    accentColor = MahiMagenta,
                    testTag = "media_type_video",
                    onClick = { onSelectMediaType(MediaType.VIDEO) },
                    modifier = Modifier.weight(1f),
                )
            }

            // Aspect Ratio Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AspectRatioOption.entries.forEach { option ->
                    val selected = option == uiState.selectedAspectRatio
                    Surface(
                        color = if (selected) MahiViolet.copy(alpha = 0.2f) else MahiSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (selected) MahiViolet else MahiOutline,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectAspectRatio(option) },
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp),
                        ) {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) MahiTextPrimary else MahiTextSecondary,
                            )
                        }
                    }
                }
            }

            // Reference Photo Selector
            ReferencePhotoPickerCard(
                photoUri = uiState.referencePhotoUri,
                onPickPhoto = onPickReferencePhoto,
                onClearPhoto = onClearReferencePhoto,
            )

            OutlinedTextField(
                value = uiState.mediaPrompt,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.media_prompt_hint),
                        color = MahiTextSecondary,
                    )
                },
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MahiMagenta,
                    unfocusedBorderColor = MahiOutline,
                    focusedContainerColor = MahiSurface,
                    unfocusedContainerColor = MahiSurface,
                    focusedTextColor = MahiTextPrimary,
                    unfocusedTextColor = MahiTextPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("media_prompt_input"),
            )

            Button(
                onClick = onGenerateMedia,
                enabled = !uiState.isGeneratingMedia,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.selectedMediaType == MediaType.PHOTO) MahiCyan else MahiMagenta,
                    contentColor = MahiObsidian,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_media_button"),
            ) {
                if (uiState.isGeneratingMedia) {
                    CircularProgressIndicator(
                        color = MahiObsidian,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = stringResource(R.string.media_generating),
                        style = MaterialTheme.typography.titleMedium,
                    )
                } else {
                    Text(
                        text = if (uiState.selectedMediaType == MediaType.PHOTO) {
                            stringResource(R.string.media_generate_photo)
                        } else {
                            stringResource(R.string.media_generate_video)
                        },
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            uiState.generatedMedia?.let { media ->
                GeneratedMediaPreviewCard(
                    media = media,
                    isPlaying = uiState.isVideoPlaying,
                    onTogglePlay = onToggleVideoPlayback,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StudioDemoNoticeBanner() {
    Surface(
        color = MahiSurface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, MahiOutline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MahiCyan,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = stringResource(R.string.studio_demo_notice),
                style = MaterialTheme.typography.labelSmall,
                color = MahiTextSecondary,
            )
        }
    }
}

@Composable
private fun MediaTypePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = if (selected) accentColor.copy(alpha = 0.16f) else MahiSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (selected) accentColor else MahiOutline),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) accentColor else MahiTextSecondary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) MahiTextPrimary else MahiTextSecondary,
            )
        }
    }
}

@Composable
private fun GeneratedMediaPreviewCard(
    media: GeneratedMediaResult,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
) {
    val effectivePhase = if (media.type == MediaType.VIDEO && isPlaying) {
        val infiniteTransition = rememberInfiniteTransition(label = "mediaReel")
        val animProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "reelProgress",
        )
        animProgress
    } else {
        0.25f
    }

    Surface(
        color = MahiSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MahiOutline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("generated_media_card"),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(media.aspectRatio.ratio.coerceIn(0.6f, 1.9f))
                    .clip(RoundedCornerShape(14.dp))
                    .background(MahiDeepNavy),
            ) {
                val referenceBitmap = rememberBitmapFromUri(media.referencePhotoUri)
                if (referenceBitmap != null) {
                    val scale = if (media.type == MediaType.VIDEO) {
                        1f + 0.08f * sin(effectivePhase * 2 * PI).toFloat()
                    } else {
                        1f
                    }
                    Image(
                        bitmap = referenceBitmap,
                        contentDescription = media.prompt,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            },
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MahiViolet.copy(alpha = 0.28f),
                                        MahiCyan.copy(alpha = 0.16f),
                                    ),
                                ),
                            ),
                    )
                } else {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val s = abs(media.seed % 100) / 100f
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MahiDeepNavy,
                                    if (media.type == MediaType.PHOTO) MahiViolet.copy(alpha = 0.45f) else MahiMagenta.copy(alpha = 0.4f),
                                    MahiCyan.copy(alpha = 0.28f),
                                ),
                            ),
                        )
                        val cx = size.width * (0.35f + 0.3f * s)
                        val cy = size.height * 0.5f
                        for (i in 0 until 18) {
                            val angle = (i * 20f + effectivePhase * 360f) * (PI / 180.0)
                            val r = size.minDimension * (0.18f + (i % 4) * 0.06f)
                            val px = cx + (r * cos(angle)).toFloat()
                            val py = cy + (r * sin(angle)).toFloat()
                            drawLine(
                                color = if (i % 2 == 0) MahiCyan else MahiMagenta,
                                start = Offset(cx, cy),
                                end = Offset(px, py),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round,
                            )
                        }
                        drawCircle(
                            color = MahiCyan.copy(alpha = 0.3f),
                            radius = size.minDimension * (0.22f + 0.08f * sin(effectivePhase * 2 * PI).toFloat()),
                            center = Offset(cx, cy),
                        )
                    }
                }

                if (media.type == MediaType.VIDEO) {
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MahiObsidian.copy(alpha = 0.75f)),
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Toggle video preview",
                            tint = MahiTextPrimary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = media.styleLabel,
                style = MaterialTheme.typography.labelMedium,
                color = if (media.type == MediaType.PHOTO) MahiCyan else MahiMagenta,
            )
            Text(
                text = "\"${media.prompt}\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MahiTextPrimary,
            )
        }
    }
}

@Composable
private fun LiveMiniAppPreview(blueprint: AppBlueprint) {
    Surface(
        color = MahiDeepNavy,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, MahiOutline),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_mini_app_preview"),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (blueprint.kind) {
                MiniAppKind.FITNESS -> {
                    var steps by remember(blueprint.appName) { mutableIntStateOf(4200) }
                    val goal = 8000
                    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = MahiCyan,
                        )
                        Text(
                            text = "Daily Goal: $steps / $goal steps",
                            style = MaterialTheme.typography.titleMedium,
                            color = MahiTextPrimary,
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        color = MahiCyan,
                        trackColor = MahiSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                    )
                    Button(
                        onClick = { steps += 500 },
                        colors = ButtonDefaults.buttonColors(containerColor = MahiCyan, contentColor = MahiObsidian),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("+500 Steps")
                    }
                }

                MiniAppKind.TODO -> {
                    val items = remember(blueprint.appName) {
                        mutableStateListOf(
                            "Set up ${blueprint.promptSummary}" to false,
                            "Customize theme & layout" to true,
                            "Share with friends" to false,
                        )
                    }
                    var newTask by remember { mutableStateOf("") }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = newTask,
                            onValueChange = { newTask = it },
                            placeholder = { Text("Add item…", color = MahiTextSecondary) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = {
                                if (newTask.isNotBlank()) {
                                    items.add(0, newTask.trim() to false)
                                    newTask = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MahiCyan),
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Add", tint = MahiObsidian)
                        }
                    }

                    items.forEachIndexed { index, (label, done) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .clickable { items[index] = label to !done },
                        ) {
                            Icon(
                                imageVector = if (done) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (done) MahiCyan else MahiTextSecondary,
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (done) MahiTextSecondary else MahiTextPrimary,
                            )
                        }
                    }
                }

                MiniAppKind.TIMER -> {
                    var seconds by remember(blueprint.appName) { mutableIntStateOf(25 * 60) }
                    var running by remember(blueprint.appName) { mutableStateOf(false) }

                    LaunchedEffect(running) {
                        while (running && seconds > 0) {
                            kotlinx.coroutines.delay(1000L)
                            seconds--
                        }
                        if (seconds == 0) running = false
                    }

                    val mins = seconds / 60
                    val secs = seconds % 60
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(Icons.Rounded.Timer, contentDescription = null, tint = MahiMagenta)
                        Text(
                            text = "%02d:%02d".format(mins, secs),
                            style = MaterialTheme.typography.displaySmall,
                            color = MahiTextPrimary,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { running = !running },
                            colors = ButtonDefaults.buttonColors(containerColor = MahiMagenta, contentColor = MahiObsidian),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(if (running) "Pause" else "Start")
                        }
                        Button(
                            onClick = {
                                running = false
                                seconds = 25 * 60
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MahiSurface, contentColor = MahiTextPrimary),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Reset")
                        }
                    }
                }

                MiniAppKind.CUSTOM -> {
                    var count by remember(blueprint.appName) { mutableIntStateOf(1) }
                    Text(
                        text = blueprint.promptSummary,
                        style = MaterialTheme.typography.titleMedium,
                        color = MahiTextPrimary,
                    )
                    Text(
                        text = "Interactive Counter: $count",
                        style = MaterialTheme.typography.titleLarge,
                        color = MahiCyan,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { count++ },
                            colors = ButtonDefaults.buttonColors(containerColor = MahiCyan, contentColor = MahiObsidian),
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("+ Action")
                        }
                        IconButton(
                            onClick = { count = 0 },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MahiSurface),
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Reset", tint = MahiTextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberBitmapFromUri(uriString: String?): ImageBitmap? {
    val context = LocalContext.current
    var bitmap by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uriString) {
        if (uriString == null) {
            bitmap = null
            return@LaunchedEffect
        }
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    return bitmap
}

@Composable
private fun ReferencePhotoPickerCard(
    photoUri: String?,
    onPickPhoto: () -> Unit,
    onClearPhoto: () -> Unit,
) {
    val thumbnail = rememberBitmapFromUri(photoUri)

    Surface(
        color = MahiSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (photoUri != null) MahiCyan.copy(alpha = 0.6f) else MahiOutline,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onPickPhoto)
            .testTag("select_reference_photo_button"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
            ) {
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail,
                        contentDescription = stringResource(R.string.media_photo_selected),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MahiDeepNavy),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhotoLibrary,
                            contentDescription = null,
                            tint = MahiCyan,
                        )
                    }
                }
                Column {
                    Text(
                        text = if (photoUri != null) {
                            stringResource(R.string.media_change_photo)
                        } else {
                            stringResource(R.string.media_select_photo)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MahiTextPrimary,
                    )
                    if (photoUri != null) {
                        Text(
                            text = stringResource(R.string.media_photo_selected),
                            style = MaterialTheme.typography.labelSmall,
                            color = MahiCyan,
                        )
                    }
                }
            }

            if (photoUri != null) {
                IconButton(
                    onClick = onClearPhoto,
                    modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Remove photo",
                        tint = MahiTextSecondary,
                    )
                }
            }
        }
    }
}
