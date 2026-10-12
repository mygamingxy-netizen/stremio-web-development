package com.uragontv.app.data.parser

import com.uragontv.app.data.model.Channel
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object M3uParser {

    fun parse(inputStream: InputStream): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = reader.readLine()

        var currentId = ""
        var currentName = ""
        var currentLogo = ""
        var currentCategory = "General"
        var currentRegion = "Nationwide"
        var currentQuality = "1080p"

        while (line != null) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                // Parse attributes
                currentId = extractAttribute(trimmed, "tvg-id")
                currentName = extractAttribute(trimmed, "tvg-name")
                currentLogo = extractAttribute(trimmed, "tvg-logo")
                currentCategory = extractAttribute(trimmed, "group-title").ifEmpty { "General" }
                currentRegion = extractAttribute(trimmed, "region").ifEmpty { "Nationwide" }
                currentQuality = extractAttribute(trimmed, "quality").ifEmpty { "1080p" }

                // Display name is after the last comma
                val commaIndex = trimmed.lastIndexOf(',')
                if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                    val displayName = trimmed.substring(commaIndex + 1).trim()
                    if (currentName.isEmpty()) {
                        currentName = displayName
                    }
                }
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                // This is the stream URL line
                val streamUrl = trimmed
                val channelId = currentId.ifEmpty { "ch_${channels.size + 1}" }
                val channelName = currentName.ifEmpty { "Channel ${channels.size + 1}" }

                channels.add(
                    Channel(
                        id = channelId,
                        name = channelName,
                        logoUrl = currentLogo,
                        streamUrl = streamUrl,
                        category = currentCategory,
                        region = currentRegion,
                        quality = currentQuality,
                        isFavorite = false,
                        currentProgram = "Live Broadcasting",
                        description = "$channelName - $currentRegion Live Transmission"
                    )
                )

                // Reset for next entry
                currentId = ""
                currentName = ""
                currentLogo = ""
                currentCategory = "General"
                currentRegion = "Nationwide"
                currentQuality = "1080p"
            }
            line = reader.readLine()
        }

        return channels
    }

    private fun extractAttribute(line: String, attributeKey: String): String {
        val pattern = """$attributeKey="([^"]*)"""".toRegex(RegexOption.IGNORE_CASE)
        val match = pattern.find(line)
        return match?.groups?.get(1)?.value?.trim() ?: ""
    }
}
