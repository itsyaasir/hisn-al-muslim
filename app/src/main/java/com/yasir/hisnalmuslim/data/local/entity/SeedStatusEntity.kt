package com.yasir.hisnalmuslim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seed_status")
data class SeedStatusEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int,
    val importedAt: Long,
)
