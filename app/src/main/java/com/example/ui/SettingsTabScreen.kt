package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.live.MahiLanguage
import com.example.live.VoiceCharacter
import com.example.permissions.SmartPermissionStatus
import com.example.permissions.SmartPermissionType
import com.example.ui.theme.MahiCyan
import com.example.ui.theme.MahiDeepNavy
import com.example.ui.theme.MahiMagenta
import com.example.ui.theme.MahiOutline
import com.example.ui.theme.MahiSurface
import com.example.ui.theme.MahiTextPrimary
import com.example.ui.theme.MahiTextSecondary
import com.example.ui.theme.MahiViolet

@Composable
fun SettingsTabScreen(
    bubbleEnabled: Boolean,
    onToggleBubble: () -> Unit,
    selectedLanguage: MahiLanguage,
    onOpenLanguageSheet: () -> Unit,
    selectedCharacter: VoiceCharacter,
    onOpenVoiceSheet: () -> Unit,
    smartPermissionStatus: SmartPermissionStatus,
    onRequestPermissionOneByOne: (SmartPermissionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeCount = SmartPermissionType.entries.count { smartPermissionStatus.isGranted(it) }
    val totalCount = SmartPermissionType.entries.size

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Pro Control Center Status Banner
            Surface(
                color = MahiSurface.copy(alpha = 0.88f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(MahiCyan.copy(alpha = 0.6f), MahiViolet.copy(alpha = 0.6f))),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MahiCyan.copy(alpha = 0.18f)),
                            ) {
                                Icon(Icons.Rounded.Shield, contentDescription = null, tint = MahiCyan)
                            }
                            Column {
                                Text("Smart Assistant Control", style = MaterialTheme.typography.titleMedium, color = MahiTextPrimary)
                                Text(
                                    "$activeCount / $totalCount Permissions Active",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MahiCyan,
                                )
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { activeCount.toFloat() / totalCount.toFloat() },
                        color = MahiCyan,
                        trackColor = MahiDeepNavy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                    )
                }
            }

            // Bubble ON/OFF Card
            Surface(
                color = MahiSurface.copy(alpha = 0.9f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, if (bubbleEnabled) Color(0xFF25D366) else MahiOutline),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Floating Bubble (Display Over Apps)", style = MaterialTheme.typography.titleMedium, color = MahiTextPrimary)
                        Text(
                            if (bubbleEnabled) "Bubble ON • Dusre apps ke upar dikhega" else "Bubble OFF • Tap karke ON karo",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (bubbleEnabled) Color(0xFF25D366) else MahiTextSecondary,
                        )
                    }
                    Switch(
                        checked = bubbleEnabled,
                        onCheckedChange = { onToggleBubble() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF25D366)),
                    )
                }
            }

            // Voice Character & Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(
                    color = MahiSurface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MahiMagenta.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenVoiceSheet),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MahiMagenta.copy(alpha = 0.16f)),
                        ) {
                            Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, tint = MahiMagenta, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Voice (20)", style = MaterialTheme.typography.labelMedium, color = MahiTextSecondary)
                        Text(selectedCharacter.displayName, style = MaterialTheme.typography.titleMedium, color = MahiTextPrimary)
                    }
                }
                Surface(
                    color = MahiSurface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MahiCyan.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenLanguageSheet),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MahiCyan.copy(alpha = 0.16f)),
                        ) {
                            Icon(Icons.Rounded.Translate, contentDescription = null, tint = MahiCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Language", style = MaterialTheme.typography.labelMedium, color = MahiTextSecondary)
                        Text(selectedLanguage.displayName, style = MaterialTheme.typography.titleMedium, color = MahiTextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.drawer_smart_permissions_title),
                style = MaterialTheme.typography.titleMedium,
                color = MahiTextPrimary,
            )
            Text(
                text = "Ek-ek karke permission do — tap karne par pehle reason batayenge.",
                style = MaterialTheme.typography.labelMedium,
                color = MahiTextSecondary,
            )

            SmartPermissionType.entries.forEach { perm ->
                val isOn = smartPermissionStatus.isGranted(perm)
                val accent = if (isOn) Color(0xFF25D366) else MahiCyan
                val icon = when (perm) {
                    SmartPermissionType.ACCESSIBILITY -> Icons.Rounded.AccessibilityNew
                    SmartPermissionType.NOTIFICATIONS -> Icons.Rounded.NotificationsActive
                    SmartPermissionType.LOCATION -> Icons.Rounded.LocationOn
                    SmartPermissionType.CONTACTS -> Icons.Rounded.Contacts
                    SmartPermissionType.OVERLAY -> Icons.Rounded.Layers
                    SmartPermissionType.CAMERA -> Icons.Rounded.CameraAlt
                    SmartPermissionType.MICROPHONE -> Icons.Rounded.Mic
                    SmartPermissionType.STORAGE -> Icons.Rounded.FolderOpen
                }
                Surface(
                    color = MahiDeepNavy.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, if (isOn) Color(0xFF25D366).copy(alpha = 0.65f) else MahiOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onRequestPermissionOneByOne(perm) },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(accent.copy(alpha = 0.14f)),
                            ) {
                                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(perm.title, style = MaterialTheme.typography.titleMedium, color = MahiTextPrimary)
                                Text(perm.reasonHinglish, style = MaterialTheme.typography.labelSmall, color = MahiTextSecondary)
                            }
                        }
                        Surface(
                            color = if (isOn) Color(0xFF25D366).copy(alpha = 0.16f) else MahiMagenta.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, if (isOn) Color(0xFF25D366) else MahiMagenta.copy(alpha = 0.6f)),
                        ) {
                            Text(
                                text = if (isOn) "ON" else "OFF",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOn) Color(0xFF25D366) else MahiMagenta,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
