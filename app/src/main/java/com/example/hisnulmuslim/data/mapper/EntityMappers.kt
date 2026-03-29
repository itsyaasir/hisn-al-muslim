package com.example.hisnulmuslim.data.mapper

import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.core.model.DhikrProgress
import com.example.hisnulmuslim.data.local.entity.DhikrEntity
import com.example.hisnulmuslim.data.local.entity.DhikrRow
import com.example.hisnulmuslim.data.local.entity.ProgressEntity
import com.example.hisnulmuslim.data.local.seed.SeedDhikr

fun DhikrRow.toModel(): Dhikr = Dhikr(
    id = id,
    title = title,
    arabicText = arabicText,
    transliteration = transliteration,
    translation = translation,
    repeatCount = repeatCount,
    notes = notes,
    sourceReference = sourceReference,
    orderIndex = orderIndex,
    tags = tags,
)

fun ProgressEntity.toModel(): DhikrProgress = DhikrProgress(
    dhikrId = dhikrId,
    currentCount = currentCount,
    completedCount = completedCount,
    updatedAt = updatedAt,
)

fun SeedDhikr.toEntity(orderIndex: Int): DhikrEntity = DhikrEntity(
    id = id,
    title = title,
    arabicText = arabicText,
    transliteration = transliteration,
    translation = translation,
    repeatCount = repeatCount,
    notes = notes,
    sourceReference = sourceReference,
    orderIndex = orderIndex,
    tags = tags,
)
