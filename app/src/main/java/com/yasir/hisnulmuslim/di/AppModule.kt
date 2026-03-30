package com.yasir.hisnulmuslim.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.yasir.hisnulmuslim.core.util.TimeProvider
import com.yasir.hisnulmuslim.data.local.dao.DhikrDao
import com.yasir.hisnulmuslim.data.local.dao.FavoriteDao
import com.yasir.hisnulmuslim.data.local.dao.ProgressDao
import com.yasir.hisnulmuslim.data.local.db.HisnulMuslimDatabase
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
