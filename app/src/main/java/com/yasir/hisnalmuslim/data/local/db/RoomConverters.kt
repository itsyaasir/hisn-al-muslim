package com.yasir.hisnalmuslim.data.local.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class RoomConverters {
    private val json = Json

    @TypeConverter
    fun fromTags(tags: List<String>?): String? = tags?.let(json::encodeToString)

    @TypeConverter
    fun toTags(rawValue: String?): List<String> = rawValue?.let(json::decodeFromString) ?: emptyList()
}
