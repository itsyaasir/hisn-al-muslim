package com.example.hisnulmuslim.data.repository

import com.example.hisnulmuslim.data.local.seed.SeedImporter
import javax.inject.Inject

class DefaultSeedRepository @Inject constructor(
    private val seedImporter: SeedImporter,
) : SeedRepository {
    override suspend fun ensureSeeded() {
        seedImporter.importIfNeeded()
    }
}
