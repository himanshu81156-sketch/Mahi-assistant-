package com.example.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.chat.ChatMessage
import com.example.live.MahiLanguage
import com.example.live.SessionState
import com.example.live.VoiceCharacter
import com.example.permissions.SmartPermissionStatus
import com.example.permissions.SmartPermissionType
import com.example.ui.theme.MahiCyan
import com.example.ui.theme.MahiDeepNavy
import com.example.ui.theme.MahiMagenta
import com.example.ui.theme.MahiObsidian
import com.example.ui.theme.MahiOutline
import com.example.ui.theme.MahiSurface
import com.example.ui.theme.MahiTextPrimary
import com.example.ui.theme.MahiTextSecondary
import com.example.ui.theme.MahiViolet
import kotlinx.coroutines.launch

enum class MahiTab {
    CHAT,
    PHOTO,
    VIDEO,
    SETTINGS,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahiAssistantScreen(
    uiState: MahiUiState,
    chatMessages: List<ChatMessage>,
    isChatReplying: Boolean,
    bubbleEnabled: Boolean,
    onToggleBubble: () -> Unit,
    onSendChatMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onCentralTap: () -> Unit,
    onSelectLanguage: (MahiLanguage) -> Unit,
    onSelectVoiceCharacter: (VoiceCharacter) -> Unit,
    onOpenUrl: (String) -> Unit,
    selectedPhotoUri: String?,
    capturedBitmap: Bitmap?,
    onTakeCameraPhoto: () -> Unit,
    onPickGalleryPhoto: () -> Unit,
    selectedVideoLabel: String?,
    onRecordCameraVideo: () -> Unit,
    onPickGalleryVideo: () -> Unit,
    grantedStorageLabel: String?,
    pickedMediaLabel: String?,
    onPickStorageFolder: () -> Unit,
    onPickMedia: () -> Unit,
    onOpenAppSettings: () -> Unit,
    smartPermissionStatus: SmartPermissionStatus,
    onGrantSmartPermission: (SmartPermissionType) -> Unit,
    onDenySmartPermission: (SmartPermissionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showVoiceSheet by remember { mutableStateOf(false) }
    var pendingPermission by remember { mutableStateOf<SmartPermissionType?>(null) }
    var selectedTab by remember { mutableStateOf(MahiTab.CHAT) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val activeAccent by animateColorAsState(
        targetValue = when (uiState.sessionState) {
            SessionState.DISCONNECTED -> MahiCyan
            SessionState.CONNECTING -> MahiViolet
            SessionState.LISTENING -> MahiCyan
            SessionState.SPEAKING -> MahiMagenta
        },
        animationSpec = tween(durationMillis = 450),
        label = "activeAccent",
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
                        .verticalScroll(rememberScrollState())
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

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MahiOutline)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.drawer_permissions_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MahiTextPrimary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    DrawerLinkCard(
                        title = stringResource(R.string.drawer_storage_folder),
                        subtitle = grantedStorageLabel ?: stringResource(R.string.drawer_storage_subtitle),
                        icon = Icons.Rounded.FolderOpen,
                        accentColor = MahiCyan,
                        testTag = "drawer_storage_button",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onPickStorageFolder()
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DrawerLinkCard(
                        title = stringResource(R.string.drawer_media_picker),
                        subtitle = pickedMediaLabel ?: stringResource(R.string.drawer_media_subtitle),
                        icon = Icons.Rounded.PhotoLibrary,
                        accentColor = MahiMagenta,
                        testTag = "drawer_media_button",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onPickMedia()
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    DrawerLinkCard(
                        title = stringResource(R.string.drawer_app_settings),
                        subtitle = stringResource(R.string.drawer_app_settings_subtitle),
                        icon = Icons.Rounded.Settings,
                        accentColor = MahiViolet,
                        testTag = "drawer_app_settings_button",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onOpenAppSettings()
                        },
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MahiOutline)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.drawer_smart_permissions_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MahiTextPrimary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SmartPermissionType.entries.forEach { permType ->
                        val isOn = smartPermissionStatus.isGranted(permType)
                        val icon = when (permType) {
                            SmartPermissionType.ACCESSIBILITY -> Icons.Rounded.AccessibilityNew
                            SmartPermissionType.NOTIFICATIONS -> Icons.Rounded.NotificationsActive
                            SmartPermissionType.LOCATION -> Icons.Rounded.LocationOn
                            SmartPermissionType.CONTACTS -> Icons.Rounded.Contacts
                            SmartPermissionType.OVERLAY -> Icons.Rounded.Layers
                            SmartPermissionType.CAMERA -> Icons.Rounded.CameraAlt
                            SmartPermissionType.MICROPHONE -> Icons.Rounded.Mic
                            SmartPermissionType.STORAGE -> Icons.Rounded.FolderOpen
                        }
                        DrawerLinkCard(
                            title = permType.title,
                            subtitle = if (isOn) {
                                stringResource(R.string.permission_status_on)
                            } else {
                                stringResource(R.string.permission_status_off)
                            },
                            icon = icon,
                            accentColor = if (isOn) Color(0xFF25D366) else MahiCyan,
                            testTag = "drawer_perm_${permType.name.lowercase()}",
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                pendingPermission = permType
                            },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        },
    ) {
        Scaffold(
            containerColor = MahiObsidian,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MahiCyan.copy(alpha = 0.55f),
                                        MahiMagenta.copy(alpha = 0.55f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                    NavigationBar(
                        containerColor = MahiSurface.copy(alpha = 0.96f),
                        contentColor = MahiTextPrimary,
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == MahiTab.CHAT,
                            onClick = { selectedTab = MahiTab.CHAT },
                            icon = { Icon(Icons.AutoMirrored.Rounded.Chat, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_chat)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MahiCyan,
                                selectedTextColor = MahiCyan,
                                indicatorColor = MahiCyan.copy(alpha = 0.18f),
                                unselectedIconColor = MahiTextSecondary,
                                unselectedTextColor = MahiTextSecondary,
                            ),
                            modifier = Modifier.testTag("tab_chat"),
                        )
                        NavigationBarItem(
                            selected = selectedTab == MahiTab.PHOTO,
                            onClick = { selectedTab = MahiTab.PHOTO },
                            icon = { Icon(Icons.Rounded.Photo, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_photo)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MahiMagenta,
                                selectedTextColor = MahiMagenta,
                                indicatorColor = MahiMagenta.copy(alpha = 0.18f),
                                unselectedIconColor = MahiTextSecondary,
                                unselectedTextColor = MahiTextSecondary,
                            ),
                            modifier = Modifier.testTag("tab_photo"),
                        )
                        NavigationBarItem(
                            selected = selectedTab == MahiTab.VIDEO,
                            onClick = { selectedTab = MahiTab.VIDEO },
                            icon = { Icon(Icons.Rounded.Videocam, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_video)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MahiCyan,
                                selectedTextColor = MahiCyan,
                                indicatorColor = MahiCyan.copy(alpha = 0.18f),
                                unselectedIconColor = MahiTextSecondary,
                                unselectedTextColor = MahiTextSecondary,
                            ),
                            modifier = Modifier.testTag("tab_video"),
                        )
                        NavigationBarItem(
                            selected = selectedTab == MahiTab.SETTINGS,
                            onClick = { selectedTab = MahiTab.SETTINGS },
                            icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_settings)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MahiViolet,
                                selectedTextColor = MahiViolet,
                                indicatorColor = MahiViolet.copy(alpha = 0.18f),
                                unselectedIconColor = MahiTextSecondary,
                                unselectedTextColor = MahiTextSecondary,
                            ),
                            modifier = Modifier.testTag("tab_settings"),
                        )
                    }
                }
            },
        ) { innerPadding ->
            val bgTransition = rememberInfiniteTransition(label = "ambientGlow")
            val glowDrift by bgTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 6500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "glowDrift",
            )

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MahiObsidian),
            ) {
                // Ambient Cyber-Glow Canvas Backdrop
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(MahiDeepNavy, MahiObsidian, MahiDeepNavy.copy(alpha = 0.7f)),
                        ),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(MahiCyan.copy(alpha = 0.16f), Color.Transparent),
                            center = Offset(size.width * (0.18f + 0.12f * glowDrift), size.height * 0.14f),
                            radius = size.minDimension * 0.65f,
                        ),
                        radius = size.minDimension * 0.65f,
                        center = Offset(size.width * (0.18f + 0.12f * glowDrift), size.height * 0.14f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(MahiMagenta.copy(alpha = 0.13f), Color.Transparent),
                            center = Offset(size.width * (0.85f - 0.12f * glowDrift), size.height * 0.75f),
                            radius = size.minDimension * 0.7f,
                        ),
                        radius = size.minDimension * 0.7f,
                        center = Offset(size.width * (0.85f - 0.12f * glowDrift), size.height * 0.75f),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                ) {
                    // Glassmorphic Top Header Bar
                    Surface(
                        color = MahiSurface.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(MahiCyan.copy(alpha = 0.45f), MahiMagenta.copy(alpha = 0.35f)),
                            ),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
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
                                Box {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_creator_logo),
                                        contentDescription = stringResource(R.string.cd_creator_logo),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .border(
                                                1.5.dp,
                                                Brush.linearGradient(listOf(MahiCyan, MahiMagenta)),
                                                CircleShape,
                                            )
                                            .clickable { coroutineScope.launch { drawerState.open() } }
                                            .testTag("creator_logo_avatar"),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366))
                                            .border(1.5.dp, MahiObsidian, CircleShape),
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.mahi_wordmark),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MahiTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = stringResource(R.string.mahi_subtitle),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MahiCyan,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                BubbleTogglePill(
                                    enabled = bubbleEnabled,
                                    onClick = {
                                        if (!smartPermissionStatus.overlayEnabled) {
                                            pendingPermission = SmartPermissionType.OVERLAY
                                        } else {
                                            onToggleBubble()
                                        }
                                    },
                                )
                                LanguageSelectorPill(
                                    selectedLanguage = uiState.selectedLanguage,
                                    accentColor = activeAccent,
                                    onClick = { showLanguageSheet = true },
                                )
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(160))
                        },
                        label = "tabTransition",
                        modifier = Modifier.weight(1f),
                    ) { tab ->
                        when (tab) {
                            MahiTab.CHAT -> {
                                ChatTabScreen(
                                    messages = chatMessages,
                                    isReplying = isChatReplying,
                                    voiceState = uiState.sessionState,
                                    onSendMessage = onSendChatMessage,
                                    onClearChat = onClearChat,
                                    onMicTap = onCentralTap,
                                    onQuickNavigatePhoto = { selectedTab = MahiTab.PHOTO },
                                    onQuickNavigateVideo = { selectedTab = MahiTab.VIDEO },
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            MahiTab.PHOTO -> {
                                PhotoStudioTab(
                                    selectedPhotoUri = selectedPhotoUri,
                                    capturedBitmap = capturedBitmap,
                                    onTakeCameraPhoto = onTakeCameraPhoto,
                                    onPickGalleryPhoto = onPickGalleryPhoto,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            MahiTab.VIDEO -> {
                                VideoStudioTab(
                                    selectedVideoLabel = selectedVideoLabel,
                                    onRecordCameraVideo = onRecordCameraVideo,
                                    onPickGalleryVideo = onPickGalleryVideo,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }

                            MahiTab.SETTINGS -> {
                                SettingsTabScreen(
                                    bubbleEnabled = bubbleEnabled,
                                    onToggleBubble = {
                                        if (!smartPermissionStatus.overlayEnabled) {
                                            pendingPermission = SmartPermissionType.OVERLAY
                                        } else {
                                            onToggleBubble()
                                        }
                                    },
                                    selectedLanguage = uiState.selectedLanguage,
                                    onOpenLanguageSheet = { showLanguageSheet = true },
                                    selectedCharacter = uiState.selectedCharacter,
                                    onOpenVoiceSheet = { showVoiceSheet = true },
                                    smartPermissionStatus = smartPermissionStatus,
                                    onRequestPermissionOneByOne = { perm ->
                                        pendingPermission = perm
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
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

                if (showVoiceSheet) {
                    VoiceCharacterPickerSheet(
                        selectedCharacter = uiState.selectedCharacter,
                        accentColor = MahiMagenta,
                        onSelect = { character ->
                            onSelectVoiceCharacter(character)
                            showVoiceSheet = false
                        },
                        onDismiss = { showVoiceSheet = false },
                    )
                }

                pendingPermission?.let { perm ->
                    AlertDialog(
                        onDismissRequest = { pendingPermission = null },
                        containerColor = MahiSurface,
                        titleContentColor = MahiTextPrimary,
                        textContentColor = MahiTextSecondary,
                        title = {
                            Text(
                                text = perm.title,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = perm.reasonHinglish,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MahiTextPrimary,
                                )
                                Text(
                                    text = stringResource(R.string.permission_ask_prompt),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MahiCyan,
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    pendingPermission = null
                                    onGrantSmartPermission(perm)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MahiCyan,
                                    contentColor = MahiObsidian,
                                ),
                            ) {
                                Text(stringResource(R.string.permission_yes))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    pendingPermission = null
                                    onDenySmartPermission(perm)
                                },
                            ) {
                                Text(
                                    text = stringResource(R.string.permission_no),
                                    color = MahiTextSecondary,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BubbleTogglePill(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val accent = if (enabled) Color(0xFF25D366) else MahiTextSecondary
    Surface(
        color = if (enabled) accent.copy(alpha = 0.16f) else MahiSurface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, if (enabled) accent else MahiOutline),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag("bubble_toggle_button"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Layers,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = if (enabled) stringResource(R.string.bubble_on) else stringResource(R.string.bubble_off),
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) accent else MahiTextPrimary,
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceCharacterPickerSheet(
    selectedCharacter: VoiceCharacter,
    accentColor: Color,
    onSelect: (VoiceCharacter) -> Unit,
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
                text = stringResource(R.string.voice_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                color = MahiTextPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.voice_sheet_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MahiTextSecondary,
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(380.dp),
            ) {
                items(VoiceCharacter.entries) { character ->
                    val isSelected = character == selectedCharacter
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
                            .clickable { onSelect(character) }
                            .testTag("voice_char_${character.name.lowercase()}"),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = character.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MahiTextPrimary,
                                )
                                Text(
                                    text = character.vibeTag,
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
