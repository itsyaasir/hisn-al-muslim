package com.example.hisnulmuslim.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favorites",
    primaryKeys = ["dhikrId"],
    foreignKeys = [
        ForeignKey(
            entity = DhikrEntity::class,
            parentColumns = ["id"],
            childColumns = ["dhikrId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("dhikrId")],
)
data class FavoriteEntity(
    val dhikrId: Long,
    val createdAt: Long,
)
