package com.example.hisnulmuslim.data.mapper

import com.example.hisnulmuslim.core.model.Dhikr
import com.example.hisnulmuslim.core.model.DhikrProgress
import com.example.hisnulmuslim.core.model.Collection
import com.example.hisnulmuslim.data.local.entity.DhikrEntity
import com.example.hisnulmuslim.data.local.entity.DhikrRow
import com.example.hisnulmuslim.data.local.entity.CollectionRow
import com.example.hisnulmuslim.data.local.entity.ProgressEntity
import com.example.hisnulmuslim.data.local.seed.SeedDhikr
import com.example.hisnulmuslim.data.local.seed.SeedCollection

fun DhikrRow.toModel(): Dhikr = Dhikr(
    id = id,
    collectionId = collectionId,
    collectionTitle = collectionTitle,
    collectionSubtitle = collectionSubtitle,
    collectionOrderIndex = collectionOrderIndex,
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

fun CollectionRow.toModel(): Collection = Collection(
    id = id,
    title = title,
    subtitle = subtitle,
    orderIndex = orderIndex,
    firstDhikrId = firstDhikrId,
    itemCount = itemCount,
)

fun ProgressEntity.toModel(): DhikrProgress = DhikrProgress(
    dhikrId = dhikrId,
    currentCount = currentCount,
    completedCount = completedCount,
    updatedAt = updatedAt,
)

fun SeedDhikr.toEntity(
    collection: SeedCollection,
    orderIndex: Int,
): DhikrEntity = DhikrEntity(
    id = id,
    collectionId = collection.id,
    collectionTitle = collection.title,
    collectionSubtitle = collection.subtitle,
    collectionOrderIndex = collection.orderIndex,
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
