package com.example.chat

import android.content.Context
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)

class ChatRepository(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("mera_ai_chat_prefs", Context.MODE_PRIVATE)

    fun loadHistory(): List<ChatMessage> {
        val raw = prefs.getString("chat_history", null) ?: return defaultHistory()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        ChatMessage(
                            id = obj.getString("id"),
                            text = obj.getString("text"),
                            isUser = obj.getBoolean("isUser"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        ),
                    )
                }
            }.ifEmpty { defaultHistory() }
        }.getOrElse { defaultHistory() }
    }

    fun saveHistory(messages: List<ChatMessage>) {
        val arr = JSONArray()
        messages.takeLast(60).forEach { msg ->
            arr.put(
                JSONObject().apply {
                    put("id", msg.id)
                    put("text", msg.text)
                    put("isUser", msg.isUser)
                    put("timestamp", msg.timestamp)
                },
            )
        }
        prefs.edit().putString("chat_history", arr.toString()).apply()
    }

    fun clearHistory(): List<ChatMessage> {
        prefs.edit().remove("chat_history").apply()
        return defaultHistory()
    }

    suspend fun generateReply(userText: String): String {
        delay(450L)
        val lower = userText.lowercase()
        return when {
            listOf("otp", "pin", "password", "aadhaar", "cvv").any { it in lower } ->
                "Bhai, OTP, Password, Bank PIN ya Aadhaar main kabhi nahi padhta aur na hi save karta hu. Safe raho!"
            listOf("call", "phone", "mummy", "papa", "message", "whatsapp").any { it in lower } ->
                "Samajh gaya! Lekin bina confirm kiye main call ya message nahi karunga. Batao, kar du? (Haan / Nahi)"
            listOf("pay", "payment", "upi", "paise", "transfer", "delete").any { it in lower } ->
                "Ruko! Payment ya delete karne se pehle confirmation zaroori hai. Kya main aage badhu? Kar du?"
            listOf("photo", "pic", "image", "selfie").any { it in lower } ->
                "Photo banana hai? Neeche Photo tab pe tap karo—waha Camera, Gallery, Background, Shirt aur Smile edit sab milega!"
            listOf("video", "reel", "shorts").any { it in lower } ->
                "Video banana hai? Neeche Video tab me jao—waha Trim, Music, Text, Speed aur 9:16 Reels save sab ready hai!"
            listOf("hello", "hi", "namaste", "kaise ho", "kya haal").any { it in lower } ->
                "Namaste dost! Main ekdum mast hu. Batao aaj kya karein—Chat, Photo editing ya Video?"
            else ->
                "Demo Mode: \"$userText\" — Badhiya! Main samajh gaya. Live Gemini AI replies ke liye Firebase enable kar sakte ho."
        }
    }

    private fun defaultHistory(): List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome_1",
            text = "Namaste! Main tumhara AI Assistant hu. Yaha se tum Chat kar sakte ho, Photo bana sakte ho, Video bana sakte ho. Batao pehle kya karu?",
            isUser = false,
        ),
    )
}
