package com.example.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

enum class SmartPermissionType(
    val title: String,
    val reasonHinglish: String,
    val grantedPrompt: String,
    val deniedFallback: String,
) {
    ACCESSIBILITY(
        title = "1. Accessibility",
        reasonHinglish = "Tumhare liye phone chalane ke liye - App kholna, button dabana.",
        grantedPrompt = "Theek hai, ab Settings me jaake Accessibility ON kar do, main yahi wait kar raha hu.",
        deniedFallback = "Koi baat nahi, bina iske main sirf chat aur voice se help karunga.",
    ),
    NOTIFICATIONS(
        title = "2. Phone Notifications",
        reasonHinglish = "Message aur call ka reply karne ke liye. Main OTP ya PIN kabhi nahi padhunga.",
        grantedPrompt = "Theek hai, ab Settings me jaake Notification access ON kar do, main yahi wait kar raha hu.",
        deniedFallback = "Koi baat nahi, main bina notifications padhe hi tumhari help karta rahunga.",
    ),
    LOCATION(
        title = "3. Location",
        reasonHinglish = "Aas-paas ki jagah aur mausam batane ke liye.",
        grantedPrompt = "Theek hai, ab Location permission Allow kar do.",
        deniedFallback = "Koi baat nahi, tum shehar ka naam bata dena, main waise hi help kar dunga.",
    ),
    CONTACTS(
        title = "4. Contacts",
        reasonHinglish = "Naam se call / message karne ke liye.",
        grantedPrompt = "Theek hai, ab Contacts permission Allow kar do.",
        deniedFallback = "Koi baat nahi, tum number bol dena, main call dial karne me help kar dunga.",
    ),
    OVERLAY(
        title = "5. Display Over Other Apps",
        reasonHinglish = "Kisi bhi app ke upar chhota bubble dikhane ke liye, taaki tumhe baar-baar app band na karna pade.",
        grantedPrompt = "Theek hai, ab Settings me jaake Display over other apps ON kar do, main yahi wait kar raha hu.",
        deniedFallback = "Koi baat nahi, bina iske main sirf apne app ke andar hi dikhunga, bahar bubble nahi aayega.",
    ),
    CAMERA(
        title = "6. Camera",
        reasonHinglish = "Photo aur Video banane ke liye.",
        grantedPrompt = "Theek hai, ab Camera permission Allow kar do.",
        deniedFallback = "Koi baat nahi, tum Gallery se photo ya video select kar sakte ho.",
    ),
    MICROPHONE(
        title = "7. Microphone",
        reasonHinglish = "Voice se baat karne ke liye.",
        grantedPrompt = "Theek hai, ab Microphone permission Allow kar do.",
        deniedFallback = "Koi baat nahi, tum Chat box me type karke baat kar sakte ho.",
    ),
    STORAGE(
        title = "8. Storage / Photos & Videos",
        reasonHinglish = "Photo aur Video save karne ke liye.",
        grantedPrompt = "Theek hai, ab Storage folder ya Photo Picker se access de do.",
        deniedFallback = "Koi baat nahi, main preview yahi app ke andar dikha dunga.",
    ),
}

data class SmartPermissionStatus(
    val accessibilityEnabled: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val locationEnabled: Boolean = false,
    val contactsEnabled: Boolean = false,
    val overlayEnabled: Boolean = false,
    val cameraEnabled: Boolean = false,
    val microphoneEnabled: Boolean = false,
    val storageEnabled: Boolean = false,
) {
    fun isGranted(type: SmartPermissionType): Boolean = when (type) {
        SmartPermissionType.ACCESSIBILITY -> accessibilityEnabled
        SmartPermissionType.NOTIFICATIONS -> notificationsEnabled
        SmartPermissionType.LOCATION -> locationEnabled
        SmartPermissionType.CONTACTS -> contactsEnabled
        SmartPermissionType.OVERLAY -> overlayEnabled
        SmartPermissionType.CAMERA -> cameraEnabled
        SmartPermissionType.MICROPHONE -> microphoneEnabled
        SmartPermissionType.STORAGE -> storageEnabled
    }
}

object SmartPermissionsChecker {
    fun checkAll(context: Context, storageGranted: Boolean = false): SmartPermissionStatus {
        val locationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        val contactsGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS,
        ) == PackageManager.PERMISSION_GRANTED

        val cameraGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED

        val micGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED

        val overlayGranted = Settings.canDrawOverlays(context)

        val notificationsGranted = NotificationManagerCompat
            .getEnabledListenerPackages(context)
            .contains(context.packageName)

        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ).orEmpty()
        val accessibilityGranted = enabledServices.contains(context.packageName)

        return SmartPermissionStatus(
            accessibilityEnabled = accessibilityGranted,
            notificationsEnabled = notificationsGranted,
            locationEnabled = locationGranted,
            contactsEnabled = contactsGranted,
            overlayEnabled = overlayGranted,
            cameraEnabled = cameraGranted,
            microphoneEnabled = micGranted,
            storageEnabled = storageGranted,
        )
    }
}
