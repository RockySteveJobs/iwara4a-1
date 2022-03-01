package com.rerere.iwara4a.di

import androidx.room.Room
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.dao.AppDatabase
import com.rerere.iwara4a.model.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSessionManager() = SessionManager()

    @Provides
    @Singleton
    fun provideDatabase() = Room.databaseBuilder(
        AppContext.instance,
        AppDatabase::class.java,
        "iwaradb"
    ).build()
}