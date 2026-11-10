package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.live.MahiToolAction
import com.example.permissions.SmartPermissionType
import com.example.permissions.SmartPermissionsChecker
import com.example.ui.MahiAssistantScreen
import com.example.ui.MahiViewModel
import com.example.ui.theme.MahiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MahiViewModel by viewModels()

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
                val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
                val isChatReplying by viewModel.isChatReplying.collectAsStateWithLifecycle()

                var grantedStorageLabel by remember { mutableStateOf<String?>(null) }
                var pickedMediaLabel by remember { mutableStateOf<String?>(null) }
                var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
                var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
                var selectedVideoLabel by remember { mutableStateOf<String?>(null) }

                var smartPermStatus by remember {
                    mutableStateOf(SmartPermissionsChecker.checkAll(context, grantedStorageLabel != null))
                }
                var bubbleEnabled by remember { mutableStateOf(smartPermStatus.overlayEnabled) }
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner, grantedStorageLabel) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            smartPermStatus = SmartPermissionsChecker.checkAll(
                                context,
                                storageGranted = grantedStorageLabel != null,
                            )
                            if (!smartPermStatus.overlayEnabled) {
                                bubbleEnabled = false
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                val micPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    viewModel.updateMicPermission(granted)
                    smartPermStatus = SmartPermissionsChecker.checkAll(context, grantedStorageLabel != null)
                }

                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) {
                    smartPermStatus = SmartPermissionsChecker.checkAll(context, grantedStorageLabel != null)
                }

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) {
                    smartPermStatus = SmartPermissionsChecker.checkAll(context, grantedStorageLabel != null)
                }

                val contactsPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) {
                    smartPermStatus = SmartPermissionsChecker.checkAll(context, grantedStorageLabel != null)
                }

                val storageFolderLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree(),
                ) { uri ->
                    if (uri != null) {
                        runCatching {
                            contentResolver.takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                            )
                        }
                        grantedStorageLabel = "Granted: ${uri.lastPathSegment ?: "Folder"}"
                        smartPermStatus = SmartPermissionsChecker.checkAll(context, storageGranted = true)
                    }
                }

                val mediaPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                ) { uri ->
                    if (uri != null) {
                        pickedMediaLabel = "Selected: ${uri.lastPathSegment ?: "Media"}"
                        selectedPhotoUri = uri.toString()
                    }
                }

                val photoGalleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                ) { uri ->
                    if (uri != null) {
                        capturedBitmap = null
                        selectedPhotoUri = uri.toString()
                        pickedMediaLabel = "Photo: ${uri.lastPathSegment ?: "Gallery"}"
                    }
                }

                val cameraPhotoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview(),
                ) { bmp ->
                    if (bmp != null) {
                        selectedPhotoUri = null
                        capturedBitmap = bmp
                    }
                }

                val videoGalleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                ) { uri ->
                    if (uri != null) {
                        selectedVideoLabel = uri.lastPathSegment ?: "Gallery Video"
                    }
                }

                val cameraVideoLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        val uri = result.data?.data
                        selectedVideoLabel = uri?.lastPathSegment ?: "Camera Video Recorded"
                    }
                }

                LaunchedEffect(Unit) {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO,
                    ) == PackageManager.PERMISSION_GRANTED
                    viewModel.updateMicPermission(granted)
                }

                LaunchedEffect(uiState.pendingToolAction) {
                    val action = uiState.pendingToolAction
                    if (action is MahiToolAction.OpenWebsite) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(action.url))
                        runCatching { context.startActivity(intent) }
                        viewModel.onToolActionConsumed()
                    }
                }

                MahiAssistantScreen(
                    uiState = uiState,
                    chatMessages = chatMessages,
                    isChatReplying = isChatReplying,
                    bubbleEnabled = bubbleEnabled,
                    onToggleBubble = {
                        if (smartPermStatus.overlayEnabled) {
                            bubbleEnabled = !bubbleEnabled
                        } else {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName"),
                            )
                            runCatching { context.startActivity(intent) }
                        }
                    },
                    onSendChatMessage = { viewModel.sendChatMessage(it) },
                    onClearChat = { viewModel.clearChatHistory() },
                    onCentralTap = { viewModel.onCentralButtonTap() },
                    onSelectLanguage = { viewModel.onSelectLanguage(it) },
                    onSelectVoiceCharacter = { viewModel.onSelectVoiceCharacter(it) },
                    onOpenUrl = { url ->
                        val action = if (url.startsWith("tel:")) Intent.ACTION_DIAL else Intent.ACTION_VIEW
                        val intent = Intent(action, Uri.parse(url))
                        runCatching { context.startActivity(intent) }
                    },
                    selectedPhotoUri = selectedPhotoUri,
                    capturedBitmap = capturedBitmap,
                    onTakeCameraPhoto = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraPhotoLauncher.launch(null)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onPickGalleryPhoto = {
                        photoGalleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                    selectedVideoLabel = selectedVideoLabel,
                    onRecordCameraVideo = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            runCatching {
                                cameraVideoLauncher.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE))
                            }
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onPickGalleryVideo = {
                        videoGalleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly),
                        )
                    },
                    grantedStorageLabel = grantedStorageLabel,
                    pickedMediaLabel = pickedMediaLabel,
                    onPickStorageFolder = { storageFolderLauncher.launch(null) },
                    onPickMedia = {
                        mediaPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                        )
                    },
                    onOpenAppSettings = {
                        val settingsIntent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null),
                        )
                        runCatching { context.startActivity(settingsIntent) }
                    },
                    smartPermissionStatus = smartPermStatus,
                    onGrantSmartPermission = { permType ->
                        viewModel.speakAssistantFeedback(permType.grantedPrompt)
                        when (permType) {
                            SmartPermissionType.ACCESSIBILITY -> {
                                runCatching {
                                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                                }
                            }
                            SmartPermissionType.NOTIFICATIONS -> {
                                runCatching {
                                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                                }
                            }
                            SmartPermissionType.LOCATION -> {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            SmartPermissionType.CONTACTS -> {
                                contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                            SmartPermissionType.OVERLAY -> {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName"),
                                )
                                runCatching { context.startActivity(intent) }
                            }
                            SmartPermissionType.CAMERA -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            SmartPermissionType.MICROPHONE -> {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                            SmartPermissionType.STORAGE -> {
                                storageFolderLauncher.launch(null)
                            }
                        }
                    },
                    onDenySmartPermission = { permType ->
                        viewModel.speakAssistantFeedback(permType.deniedFallback)
                    },
                )
            }
        }
    }
}
