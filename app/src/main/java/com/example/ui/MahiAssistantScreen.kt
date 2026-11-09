package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.live.MahiLanguage
import com.example.live.SessionState
import com.example.studio.AspectRatioOption
import com.example.studio.MediaType
import com.example.studio.StudioUiState
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
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch

enum class MahiTab {
    VOICE,
    APP_BUILDER,
    MEDIA_STUDIO,
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MahiAssistantScreen(
    uiState: MahiUiState,
    studioState: StudioUiState,
    lastExecutedToolUrl: String?,
    onCentralTap: () -> Unit,
    onEndSession: () -> Unit,
    onSelectLanguage: (MahiLanguage) -> Unit,
    onRequestMicPermission: () -> Unit,
    onTriggerSampleTurn: () -> Unit,
    onTriggerWebsiteTool: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onAppPromptChange: (String) -> Unit,
    onBuildApp: () -> Unit,
    onMediaPromptChange: (String) -> Unit,
    onSelectMediaType: (MediaType) -> Unit,
    onSelectAspectRatio: (AspectRatioOption) -> Unit,
    onToggleVideoPlayback: () -> Unit,
    onGenerateMedia: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLanguageSheet by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(MahiTab.VOICE) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val activeAccent by animateColorAsState(
        targetValue = when (uiState.sessionState) {
            SessionState.DISCONNECTED -> MahiViolet
            SessionState.CONNECTING -> MahiCyan
            SessionState.LISTENING -> MahiCyan
            SessionState.SPEAKING -> MahiMagenta
        },
        animationSpec = tween(durationMillis = 450),
        label = "activeAccent",
    )

    val activeLevel = when (uiState.sessionState) {
        SessionState.LISTENING -> uiState.micLevel
        SessionState.SPEAKING -> uiState.speakerLevel
        SessionState.CONNECTING -> 0.35f
        SessionState.DISCONNECTED -> 0.05f
    }

    val smoothLevel by animateFloatAsState(
        targetValue = activeLevel,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 400f),
        label = "smoothLevel",
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MahiSurface,
                drawerContentColor = MahiTextPrimary,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(24.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_creator_logo),
                            contentDescription = stringResource(R.string.cd_creator_logo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, activeAccent, CircleShape),
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.drawer_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MahiTextPrimary,
                            )
                            Text(
                                text = stringResource(R.string.drawer_subtitle),
                                style = MaterialTheme.typography.labelMedium,
                                color = MahiTextSecondary,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MahiOutline)
                    Spacer(modifier = Modifier.height(20.dp))

                    DrawerLinkCard(
                        title = stringResource(R.string.action_call_phone),
                        subtitle = stringResource(R.string.drawer_phone_subtitle),
                        icon = Icons.Rounded.Phone,
                        accentColor = MahiMagenta,
                        testTag = "drawer_phone_button",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onOpenUrl("tel:8115894656")
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DrawerLinkCard(
                        title = stringResource(R.string.action_telegram),
                        subtitle = stringResource(R.string.drawer_telegram_subtitle),
                        icon = Icons.AutoMirrored.Rounded.Send,
                        accentColor = MahiCyan,
                        testTag = "drawer_telegram_button",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onOpenUrl("https://t.me/Griw10k")
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DrawerLinkCard(
                        title = stringResource(R.string.action_whatsapp),
                        subtitle = stringResource(R.string.drawer_whatsapp_subtitle),
                        icon = Icons.Rounded.Forum,
                        accentColor = Color(0xFF25D366),
                        testTag = "drawer_whatsapp_button",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onOpenUrl("https://whatsapp.com/channel/0029Vb75ZV26mYPSxjizl52m")
                        },
                    )
                }
            }
        },
    ) {
        Scaffold(
            containerColor = MahiObsidian,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar(
                    containerColor = MahiSurface.copy(alpha = 0.96f),
                    contentColor = MahiTextPrimary,
                ) {
                    NavigationBarItem(
                        selected = selectedTab == MahiTab.VOICE,
                        onClick = { selectedTab = MahiTab.VOICE },
                        icon = { Icon(Icons.Rounded.Mic, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_voice)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = activeAccent,
                            selectedTextColor = activeAccent,
                            indicatorColor = activeAccent.copy(alpha = 0.16f),
                            unselectedIconColor = MahiTextSecondary,
                            unselectedTextColor = MahiTextSecondary,
                        ),
                        modifier = Modifier.testTag("tab_voice"),
                    )
                    NavigationBarItem(
                        selected = selectedTab == MahiTab.APP_BUILDER,
                        onClick = { selectedTab = MahiTab.APP_BUILDER },
                        icon = { Icon(Icons.Rounded.Build, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_app_builder)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MahiCyan,
                            selectedTextColor = MahiCyan,
                            indicatorColor = MahiCyan.copy(alpha = 0.16f),
                            unselectedIconColor = MahiTextSecondary,
                            unselectedTextColor = MahiTextSecondary,
                        ),
                        modifier = Modifier.testTag("tab_app_builder"),
                    )
                    NavigationBarItem(
                        selected = selectedTab == MahiTab.MEDIA_STUDIO,
                        onClick = { selectedTab = MahiTab.MEDIA_STUDIO },
                        icon = { Icon(Icons.Rounded.PermMedia, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_media_studio)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MahiMagenta,
                            selectedTextColor = MahiMagenta,
                            indicatorColor = MahiMagenta.copy(alpha = 0.16f),
                            unselectedIconColor = MahiTextSecondary,
                            unselectedTextColor = MahiTextSecondary,
                        ),
                        modifier = Modifier.testTag("tab_media_studio"),
                    )
                }
            },
        ) { innerPadding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                activeAccent.copy(alpha = 0.16f),
                                MahiDeepNavy,
                                MahiObsidian,
                            ),
                            radius = 1400f,
                        ),
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("side_menu_button"),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Menu,
                                    contentDescription = stringResource(R.string.cd_side_menu),
                                    tint = MahiTextPrimary,
                                )
                            }
                            Image(
                                painter = painterResource(id = R.drawable.img_creator_logo),
                                contentDescription = stringResource(R.string.cd_creator_logo),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, activeAccent, CircleShape)
                                    .clickable { coroutineScope.launch { drawerState.open() } }
                                    .testTag("creator_logo_avatar"),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.mahi_wordmark),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MahiTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = stringResource(R.string.mahi_subtitle),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MahiTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            LanguageSelectorPill(
                                selectedLanguage = uiState.selectedLanguage,
                                accentColor = activeAccent,
                                onClick = { showLanguageSheet = true },
                            )
                            StatusBadge(
                                sessionState = uiState.sessionState,
                                accentColor = activeAccent,
                            )
                        }
                    }

                    when (selectedTab) {
                        MahiTab.VOICE -> {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                            ) {
                                val isCompactHeight = maxHeight < 580.dp
                                val orbSize: Dp = if (isCompactHeight) 200.dp else 260.dp

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .widthIn(max = 560.dp)
                                        .align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    if (uiState.isDemoMode) {
                                        Surface(
                                            color = MahiSurface.copy(alpha = 0.85f),
                                            shape = RoundedCornerShape(50),
                                            border = BorderStroke(1.dp, MahiOutline),
                                            modifier = Modifier.testTag("demo_mode_notice"),
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
                                                    text = stringResource(R.string.demo_mode_notice),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MahiTextSecondary,
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }

                                    // Centerpiece: Neural Voice Orb + Central Mic/Power Button
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(orbSize),
                                    ) {
                                        MahiNeuralOrb(
                                            sessionState = uiState.sessionState,
                                            level = smoothLevel,
                                            accentColor = activeAccent,
                                            modifier = Modifier.fillMaxSize(),
                                        )

                                        CentralVoiceButton(
                                            sessionState = uiState.sessionState,
                                            accentColor = activeAccent,
                                            onClick = onCentralTap,
                                        )
                                    }

                                    // Bottom Controls & State Prompt
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        AnimatedVisibility(
                                            visible = lastExecutedToolUrl != null,
                                            enter = fadeIn(),
                                            exit = fadeOut(),
                                        ) {
                                            lastExecutedToolUrl?.let { url ->
                                                Surface(
                                                    color = MahiCyan.copy(alpha = 0.14f),
                                                    shape = RoundedCornerShape(16.dp),
                                                    border = BorderStroke(1.dp, MahiCyan.copy(alpha = 0.45f)),
                                                    modifier = Modifier
                                                        .padding(bottom = 10.dp)
                                                        .testTag("tool_action_banner"),
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Language,
                                                            contentDescription = null,
                                                            tint = MahiCyan,
                                                            modifier = Modifier.size(16.dp),
                                                        )
                                                        Text(
                                                            text = stringResource(R.string.tool_executed_prefix, url),
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = MahiTextPrimary,
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        val promptText = when {
                                            uiState.sessionState == SessionState.DISCONNECTED ->
                                                stringResource(R.string.hint_disconnected)
                                            uiState.sessionState == SessionState.CONNECTING ->
                                                stringResource(R.string.hint_connecting)
                                            uiState.sessionState == SessionState.SPEAKING ->
                                                stringResource(R.string.hint_speaking)
                                            !uiState.hasMicPermission ->
                                                stringResource(R.string.hint_mic_denied)
                                            else ->
                                                stringResource(R.string.hint_listening)
                                        }

                                        Text(
                                            text = promptText,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MahiTextPrimary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.testTag("session_prompt_text"),
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            if (!uiState.hasMicPermission) {
                                                ActionChip(
                                                    label = stringResource(R.string.action_grant_mic),
                                                    icon = Icons.Rounded.MicOff,
                                                    accentColor = MahiRose,
                                                    testTag = "grant_mic_button",
                                                    onClick = onRequestMicPermission,
                                                )
                                            }

                                            if (uiState.sessionState == SessionState.LISTENING ||
                                                uiState.sessionState == SessionState.SPEAKING
                                            ) {
                                                ActionChip(
                                                    label = stringResource(R.string.action_sample_reply),
                                                    icon = Icons.Rounded.GraphicEq,
                                                    accentColor = MahiMagenta,
                                                    testTag = "sample_reply_button",
                                                    onClick = onTriggerSampleTurn,
                                                )
                                                ActionChip(
                                                    label = stringResource(R.string.action_test_tool),
                                                    icon = Icons.Rounded.Language,
                                                    accentColor = MahiCyan,
                                                    testTag = "test_tool_button",
                                                    onClick = onTriggerWebsiteTool,
                                                )
                                                ActionChip(
                                                    label = stringResource(R.string.action_end_session),
                                                    icon = Icons.Rounded.PowerSettingsNew,
                                                    accentColor = MahiRose,
                                                    testTag = "end_session_button",
                                                    onClick = onEndSession,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }

                        MahiTab.APP_BUILDER -> {
                            AppBuilderScreen(
                                uiState = studioState,
                                onPromptChange = onAppPromptChange,
                                onBuildApp = onBuildApp,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

                        MahiTab.MEDIA_STUDIO -> {
                            MediaStudioScreen(
                                uiState = studioState,
                                onPromptChange = onMediaPromptChange,
                                onSelectMediaType = onSelectMediaType,
                                onSelectAspectRatio = onSelectAspectRatio,
                                onToggleVideoPlayback = onToggleVideoPlayback,
                                onGenerateMedia = onGenerateMedia,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }

                if (showLanguageSheet) {
                    LanguagePickerSheet(
                        selectedLanguage = uiState.selectedLanguage,
                        accentColor = activeAccent,
                        onSelect = { language ->
                            onSelectLanguage(language)
                            showLanguageSheet = false
                        },
                        onDismiss = { showLanguageSheet = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    sessionState: SessionState,
    accentColor: Color,
) {
    val label = when (sessionState) {
        SessionState.DISCONNECTED -> stringResource(R.string.state_disconnected)
        SessionState.CONNECTING -> stringResource(R.string.state_connecting)
        SessionState.LISTENING -> stringResource(R.string.state_listening)
        SessionState.SPEAKING -> stringResource(R.string.state_speaking)
    }

    Surface(
        color = accentColor.copy(alpha = 0.14f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = Modifier.testTag("status_badge"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
            )
        }
    }
}

@Composable
private fun MahiNeuralOrb(
    sessionState: SessionState,
    level: Float,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyberWave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (sessionState == SessionState.CONNECTING) 900 else 1800,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wavePhase",
    )

    val wavePath = remember { Path() }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)

        // Wide ambient core glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.28f + level * 0.30f),
                    MahiViolet.copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                center = center,
                radius = size.minDimension * 0.52f,
            ),
            center = center,
            radius = size.minDimension * 0.52f,
        )

        // Symmetrical 28-bar horizontal neon equalizer spectrum (no spinning circle)
        val halfBars = 14
        val barWidth = 5.dp.toPx()
        val gap = (size.width * 0.42f) / halfBars
        val centerGap = 60.dp.toPx()

        for (i in 0 until halfBars) {
            val normDist = i.toFloat() / halfBars.toFloat()
            val wave = 0.5f + 0.5f * sin(wavePhase * 2f - i * 0.55f)
            val envelope = 1f - normDist * 0.65f
            val baseHeight = when (sessionState) {
                SessionState.DISCONNECTED -> 10.dp.toPx() * envelope
                SessionState.CONNECTING -> (16.dp.toPx() + 26.dp.toPx() * wave) * envelope
                SessionState.LISTENING, SessionState.SPEAKING ->
                    (14.dp.toPx() + (64.dp.toPx() * level * (0.4f + 0.6f * wave))) * envelope
            }

            val barColor = if (i % 2 == 0) accentColor else MahiMagenta
            val xRight = center.x + centerGap + i * gap
            val xLeft = center.x - centerGap - i * gap

            drawLine(
                color = barColor.copy(alpha = 0.9f - normDist * 0.35f),
                start = Offset(xRight, center.y - baseHeight),
                end = Offset(xRight, center.y + baseHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = barColor.copy(alpha = 0.9f - normDist * 0.35f),
                start = Offset(xLeft, center.y - baseHeight),
                end = Offset(xLeft, center.y + baseHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round,
            )
        }

        // Flowing dual energy wave across the horizon
        wavePath.reset()
        val step = 8f
        val amplitude = when (sessionState) {
            SessionState.DISCONNECTED -> 4.dp.toPx()
            SessionState.CONNECTING -> 14.dp.toPx()
            SessionState.LISTENING, SessionState.SPEAKING -> 10.dp.toPx() + 28.dp.toPx() * level
        }
        var x = 0f
        while (x <= size.width) {
            val nx = (x / size.width) * 2f - 1f
            val fade = (1f - nx * nx).coerceIn(0f, 1f)
            val y = center.y + sin(nx * 4.5f + wavePhase) * amplitude * fade
            if (x == 0f) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
            x += step
        }
        drawPath(
            path = wavePath,
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, accentColor, MahiMagenta, Color.Transparent),
            ),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun CentralVoiceButton(
    sessionState: SessionState,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val icon = when (sessionState) {
        SessionState.DISCONNECTED -> Icons.Rounded.PowerSettingsNew
        SessionState.CONNECTING -> Icons.Rounded.GraphicEq
        SessionState.LISTENING -> Icons.Rounded.Mic
        SessionState.SPEAKING -> Icons.Rounded.Stop
    }

    val interactionSource = remember { MutableInteractionSource() }
    val coreShape = RoundedCornerShape(30.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .minimumInteractiveComponentSize()
            .clip(coreShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MahiSurface,
                        MahiDeepNavy,
                    ),
                ),
            )
            .border(
                width = 2.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(accentColor, MahiMagenta),
                ),
                shape = coreShape,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = accentColor),
                onClick = onClick,
            )
            .testTag("central_mic_button"),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.cd_central_mic_button),
            tint = accentColor,
            modifier = Modifier.size(38.dp),
        )
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
) {
    Surface(
        color = MahiSurface,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag(testTag),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MahiTextPrimary,
            )
        }
    }
}

@Composable
private fun LanguageSelectorPill(
    selectedLanguage: MahiLanguage,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Surface(
        color = MahiSurface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, MahiOutline),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag("language_selector_button"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Translate,
                contentDescription = stringResource(R.string.cd_select_language),
                tint = accentColor,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = selectedLanguage.shortCode,
                style = MaterialTheme.typography.labelSmall,
                color = MahiTextPrimary,
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = MahiTextSecondary,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePickerSheet(
    selectedLanguage: MahiLanguage,
    accentColor: Color,
    onSelect: (MahiLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MahiSurface,
        contentColor = MahiTextPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.language_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                color = MahiTextPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.language_sheet_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MahiTextSecondary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(MahiLanguage.entries) { language ->
                    val isSelected = language == selectedLanguage
                    Surface(
                        color = if (isSelected) accentColor.copy(alpha = 0.14f) else MahiDeepNavy,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) accentColor else MahiOutline,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelect(language) }
                            .testTag("language_option_${language.name.lowercase()}"),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = language.nativeLabel,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MahiTextPrimary,
                                )
                                Text(
                                    text = language.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MahiTextSecondary,
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerLinkCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit,
) {
    Surface(
        color = MahiDeepNavy,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f)),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MahiTextPrimary,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MahiTextSecondary,
                )
            }
        }
    }
}
