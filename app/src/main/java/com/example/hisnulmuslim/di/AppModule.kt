package com.example.hisnulmuslim.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.hisnulmuslim.core.util.TimeProvider
import com.example.hisnulmuslim.data.local.dao.DhikrDao
import com.example.hisnulmuslim.data.local.dao.FavoriteDao
import com.example.hisnulmuslim.data.local.dao.ProgressDao
import com.example.hisnulmuslim.data.local.db.HisnulMuslimDatabase
import com.example.hisnulmuslim.data.local.seed.AssetSeedDataSource
import com.example.hisnulmuslim.data.local.seed.SeedDataSource
import com.example.hisnulmuslim.data.repository.DefaultSeedRepository
import com.example.hisnulmuslim.data.repository.DefaultSettingsRepository
import com.example.hisnulmuslim.data.repository.SeedRepository
import com.example.hisnulmuslim.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings",
)

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): HisnulMuslimDatabase {
        return Room.databaseBuilder(
            context,
            HisnulMuslimDatabase::class.java,
            "hisnul_muslim.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideDhikrDao(database: HisnulMuslimDatabase): DhikrDao = database.dhikrDao()

    @Provides
    fun provideFavoriteDao(database: HisnulMuslimDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideProgressDao(database: HisnulMuslimDatabase): ProgressDao = database.progressDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.settingsDataStore
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = TimeProvider(System::currentTimeMillis)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModule {

    @Binds
    abstract fun bindSeedRepository(
        repository: DefaultSeedRepository,
    ): SeedRepository

    @Binds
    abstract fun bindSeedDataSource(
        dataSource: AssetSeedDataSource,
    ): SeedDataSource

    @Binds
    abstract fun bindSettingsRepository(
        repository: DefaultSettingsRepository,
    ): SettingsRepository
}
