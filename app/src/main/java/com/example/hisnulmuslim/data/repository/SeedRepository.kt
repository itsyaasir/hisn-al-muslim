package com.example.hisnulmuslim.data.repository

interface SeedRepository {
    suspend fun ensureSeeded()
}
