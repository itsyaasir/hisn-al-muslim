package com.example.hisnulmuslim.data.local.seed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.serialization.json.Json

class AssetSeedDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val json: Json,
) {
    suspend fun loadDataset(): SeedDataset {
        return context.assets.open(SEED_FILE_NAME).bufferedReader().use { reader ->
            json.decodeFromString<SeedDataset>(reader.readText())
        }
    }

    companion object {
        const val SEED_FILE_NAME = "hisnul_muslim_seed.json"
    }
}
