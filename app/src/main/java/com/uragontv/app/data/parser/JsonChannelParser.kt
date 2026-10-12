package com.uragontv.app.data.parser

import com.uragontv.app.data.model.Channel
import org.json.JSONArray
import java.io.InputStream

object JsonChannelParser {

    fun parse(inputStream: InputStream): List<Channel> {
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val channels = mutableListOf<Channel>()
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val channel = Channel(
                id = obj.optString("id", "ch_$i"),
                name = obj.optString("name", "Channel $i"),
                logoUrl = obj.optString("logoUrl", ""),
                streamUrl = obj.optString("streamUrl", ""),
                category = obj.optString("category", "General"),
                region = obj.optString("region", "Nationwide"),
                quality = obj.optString("quality", "1080p"),
                isFavorite = obj.optBoolean("isFavorite", false),
                currentProgram = obj.optString("currentProgram", "Live Broadcasting"),
                description = obj.optString("description", "")
            )
            channels.add(channel)
        }

        return channels
    }
}
