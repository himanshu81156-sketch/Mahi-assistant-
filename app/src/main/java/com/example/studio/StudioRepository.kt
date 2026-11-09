package com.example.studio

import kotlinx.coroutines.delay

enum class MediaType {
    PHOTO,
    VIDEO,
}

enum class AspectRatioOption(val label: String, val ratio: Float) {
    SQUARE("1:1", 1f),
    LANDSCAPE("16:9", 16f / 9f),
    PORTRAIT("9:16", 9f / 16f),
}

enum class MiniAppKind {
    FITNESS,
    TODO,
    TIMER,
    CUSTOM,
}

data class AppBlueprint(
    val appName: String,
    val tagline: String,
    val kind: MiniAppKind,
    val promptSummary: String,
    val screens: List<String>,
    val features: List<String>,
    val sampleCode: String,
    val isDemo: Boolean = true,
)

data class GeneratedMediaResult(
    val id: String,
    val type: MediaType,
    val prompt: String,
    val aspectRatio: AspectRatioOption,
    val styleLabel: String,
    val seed: Int,
    val isDemo: Boolean = true,
)

/**
 * Single repository interface for App Builder, Photo, and Video generation.
 * Backed by [DemoStudioRepository] when Firebase AI Logic is not enabled.
 */
interface StudioRepository {
    val isDemoMode: Boolean
    suspend fun generateAppBlueprint(prompt: String): AppBlueprint
    suspend fun generateMedia(
        prompt: String,
        type: MediaType,
        aspectRatio: AspectRatioOption,
    ): GeneratedMediaResult
}

class DemoStudioRepository : StudioRepository {
    override val isDemoMode: Boolean = true

    override suspend fun generateAppBlueprint(prompt: String): AppBlueprint {
        delay(750L)
        val cleanPrompt = prompt.trim().ifBlank { "Smart Daily Companion" }
        val lower = cleanPrompt.lowercase()
        val kind = when {
            listOf("fit", "workout", "gym", "step", "health", "run").any { it in lower } -> MiniAppKind.FITNESS
            listOf("todo", "task", "note", "list", "check", "expense", "shop").any { it in lower } -> MiniAppKind.TODO
            listOf("timer", "pomodoro", "clock", "focus", "stopwatch").any { it in lower } -> MiniAppKind.TIMER
            else -> MiniAppKind.CUSTOM
        }
        val title = cleanPrompt
            .split(" ")
            .take(3)
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }

        return AppBlueprint(
            appName = "$title (Demo)",
            tagline = "Demo App Builder • Live AI generation requires Firebase",
            kind = kind,
            promptSummary = cleanPrompt,
            screens = listOf(
                "Home Dashboard — Real-time overview & quick actions",
                "Interactive Studio — Core workflow for \"$cleanPrompt\"",
                "Profile & Settings — Theme, notifications & sync",
            ),
            features = listOf(
                "Material 3 dark-mode Compose UI",
                "MVVM ViewModel + StateFlow state management",
                "Offline-ready local state & smooth animations",
            ),
            sampleCode = """
                @Composable
                fun ${title.replace(" ", "")}Screen() {
                    Scaffold { padding ->
                        Column(Modifier.padding(padding).padding(20.dp)) {
                            Text("$title", style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(12.dp))
                            Text("Generated for: $cleanPrompt")
                        }
                    }
                }
            """.trimIndent(),
            isDemo = true,
        )
    }

    override suspend fun generateMedia(
        prompt: String,
        type: MediaType,
        aspectRatio: AspectRatioOption,
    ): GeneratedMediaResult {
        delay(950L)
        val cleanPrompt = prompt.trim().ifBlank { "Futuristic neon cyberpunk skyline" }
        return GeneratedMediaResult(
            id = "demo_${System.currentTimeMillis()}",
            type = type,
            prompt = cleanPrompt,
            aspectRatio = aspectRatio,
            styleLabel = if (type == MediaType.PHOTO) "Demo Photo Render" else "Demo Motion Reel (6s)",
            seed = cleanPrompt.hashCode(),
            isDemo = true,
        )
    }
}
