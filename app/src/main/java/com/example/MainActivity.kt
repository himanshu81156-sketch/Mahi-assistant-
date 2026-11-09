package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.live.MahiToolAction
import com.example.studio.StudioViewModel
import com.example.ui.MahiAssistantScreen
import com.example.ui.MahiViewModel
import com.example.ui.theme.MahiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MahiViewModel by viewModels()
    private val studioViewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            MahiTheme {
                val context = LocalContext.current
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val studioState by studioViewModel.uiState.collectAsStateWithLifecycle()
                var lastToolUrl by remember { mutableStateOf<String?>(null) }

                val micPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    viewModel.updateMicPermission(granted)
                }

                LaunchedEffect(Unit) {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO,
                    ) == PackageManager.PERMISSION_GRANTED
                    viewModel.updateMicPermission(granted)
                    if (!granted) {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }

                LaunchedEffect(uiState.pendingToolAction) {
                    val action = uiState.pendingToolAction
                    if (action is MahiToolAction.OpenWebsite) {
                        lastToolUrl = action.label
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                        runCatching { context.startActivity(intent) }
                        viewModel.onToolActionConsumed()
                    }
                }

                MahiAssistantScreen(
                    uiState = uiState,
                    studioState = studioState,
                    lastExecutedToolUrl = lastToolUrl,
                    onCentralTap = { viewModel.onCentralButtonTap() },
                    onEndSession = { viewModel.onEndSession() },
                    onSelectLanguage = { viewModel.onSelectLanguage(it) },
                    onRequestMicPermission = {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    onTriggerSampleTurn = { viewModel.onTriggerSampleTurn() },
                    onTriggerWebsiteTool = { viewModel.onTriggerWebsiteTool() },
                    onOpenUrl = { url ->
                        val action = if (url.startsWith("tel:")) Intent.ACTION_DIAL else Intent.ACTION_VIEW
                        val intent = Intent(action, Uri.parse(url))
                        runCatching { context.startActivity(intent) }
                    },
                    onAppPromptChange = { studioViewModel.onAppPromptChange(it) },
                    onBuildApp = { studioViewModel.buildApp() },
                    onMediaPromptChange = { studioViewModel.onMediaPromptChange(it) },
                    onSelectMediaType = { studioViewModel.onSelectMediaType(it) },
                    onSelectAspectRatio = { studioViewModel.onSelectAspectRatio(it) },
                    onToggleVideoPlayback = { studioViewModel.toggleVideoPlayback() },
                    onGenerateMedia = { studioViewModel.generateMedia() },
                )
            }
        }
    }
}
