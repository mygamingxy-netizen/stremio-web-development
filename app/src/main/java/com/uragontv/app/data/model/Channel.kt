package com.uragontv.app.data.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val streamUrl: String,
    val category: String,
    val region: String,
    val quality: String = "1080p",
    val isFavorite: Boolean = false,
    val currentProgram: String = "Live Broadcast",
    val description: String = ""
)

enum class AspectRatioMode(val label: String) {
    FIT("Fit (16:9)"),
    ZOOM("Zoom / Fill"),
    STRETCH("Stretch")
}

enum class BufferProfile(val label: String, val minBufferMs: Int, val maxBufferMs: Int) {
    LOW_LATENCY("Low Latency (Fast)", 1500, 5000),
    STANDARD("Standard (Balanced)", 3000, 15000),
    HIGH_STABILITY("High Stability (Slow Conn)", 8000, 30000)
}
