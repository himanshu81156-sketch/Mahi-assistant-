package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MovieCreation
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.chat.ChatMessage
import com.example.live.SessionState
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatTabScreen(
    messages: List<ChatMessage>,
    isReplying: Boolean,
    voiceState: SessionState,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onMicTap: () -> Unit,
    onQuickNavigatePhoto: () -> Unit,
    onQuickNavigateVideo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val pulseTransition = rememberInfiniteTransition(label = "chatPulse")
    val micPulse by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "micPulse",
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Futuristic AI Command Center Hero Card
        Surface(
            color = MahiSurface.copy(alpha = 0.88f),
            shape = RoundedCornerShape(22.dp),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(MahiCyan.copy(alpha = 0.6f), MahiMagenta.copy(alpha = 0.5f)),
                ),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MahiCyan.copy(alpha = 0.10f),
                                MahiViolet.copy(alpha = 0.08f),
                                MahiMagenta.copy(alpha = 0.10f),
                            ),
                        ),
                    )
                    .padding(14.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(MahiCyan, MahiMagenta)),
                                    ),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = MahiObsidian,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            Column {
                                Text(
                                    text = "Mera AI Command Hub",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MahiTextPrimary,
                                )
                                Text(
                                    text = stringResource(R.string.demo_mode_notice),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MahiTextSecondary,
                                )
                            }
                        }
                        IconButton(
                            onClick = onClearChat,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MahiDeepNavy.copy(alpha = 0.8f))
                                .testTag("clear_chat_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteSweep,
                                contentDescription = "Clear chat history",
                                tint = MahiTextSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        QuickActionPill(
                            label = "Chat Karo",
                            icon = Icons.AutoMirrored.Rounded.Chat,
                            accent = MahiCyan,
                        ) {
                            onSendMessage("Namaste! Kya haal hai?")
                        }
                        QuickActionPill(
                            label = "Photo Banao",
                            icon = Icons.Rounded.AutoFixHigh,
                            accent = MahiMagenta,
                            onClick = onQuickNavigatePhoto,
                        )
                        QuickActionPill(
                            label = "Video Banao",
                            icon = Icons.Rounded.MovieCreation,
                            accent = MahiViolet,
                            onClick = onQuickNavigateVideo,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // WhatsApp-style Dark Neon Chat Bubbles
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            items(messages, key = { it.id }) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    if (!msg.isUser) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(end = 8.dp, bottom = 2.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MahiDeepNavy)
                                .border(1.dp, MahiMagenta.copy(alpha = 0.7f), CircleShape),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = MahiMagenta,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }

                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (msg.isUser) 20.dp else 6.dp,
                            bottomEnd = if (msg.isUser) 6.dp else 20.dp,
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = if (msg.isUser) {
                                Brush.linearGradient(listOf(MahiCyan.copy(alpha = 0.7f), MahiViolet.copy(alpha = 0.6f)))
                            } else {
                                Brush.linearGradient(listOf(MahiOutline, MahiMagenta.copy(alpha = 0.35f)))
                            },
                        ),
                        modifier = Modifier.widthIn(max = 290.dp),
                    ) {
                        Box(
                            modifier = Modifier.background(
                                if (msg.isUser) {
                                    Brush.linearGradient(
                                        listOf(MahiCyan.copy(alpha = 0.24f), MahiViolet.copy(alpha = 0.22f)),
                                    )
                                } else {
                                    Brush.linearGradient(
                                        listOf(MahiSurface.copy(alpha = 0.95f), MahiDeepNavy.copy(alpha = 0.95f)),
                                    )
                                },
                            ),
                        ) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MahiTextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            )
                        }
                    }
                }
            }

            if (isReplying) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(start = 6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = MahiCyan,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.loading_hindi),
                            style = MaterialTheme.typography.labelMedium,
                            color = MahiCyan,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Glowing Input Dock
        Surface(
            color = MahiSurface.copy(alpha = 0.92f),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, MahiOutline),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val isVoiceActive = voiceState == SessionState.LISTENING || voiceState == SessionState.SPEAKING
                IconButton(
                    onClick = onMicTap,
                    modifier = Modifier
                        .size(46.dp)
                        .scale(if (isVoiceActive) micPulse else 1f)
                        .clip(CircleShape)
                        .background(
                            if (isVoiceActive) {
                                Brush.linearGradient(listOf(MahiRose, MahiMagenta))
                            } else {
                                Brush.linearGradient(listOf(MahiDeepNavy, MahiSurface))
                            },
                        )
                        .border(1.dp, if (isVoiceActive) MahiRose else MahiCyan.copy(alpha = 0.5f), CircleShape)
                        .testTag("chat_mic_button"),
                ) {
                    Icon(
                        imageVector = if (voiceState == SessionState.SPEAKING) Icons.Rounded.Stop else Icons.Rounded.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isVoiceActive) MahiObsidian else MahiCyan,
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text("Hinglish me likho ya mic dabao…", color = MahiTextSecondary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MahiTextPrimary,
                        unfocusedTextColor = MahiTextPrimary,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_text_input"),
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(MahiCyan, MahiMagenta)),
                        )
                        .testTag("chat_send_button"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send",
                        tint = MahiObsidian,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionPill(
    label: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
) {
    Surface(
        color = MahiDeepNavy.copy(alpha = 0.9f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MahiTextPrimary,
            )
        }
    }
}
