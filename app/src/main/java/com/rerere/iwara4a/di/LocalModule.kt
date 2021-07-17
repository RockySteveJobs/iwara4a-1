package com.rerere.iwara4a.di

import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.SettingRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    @Provides
    @Singleton
    fun provideSessionManager() = SessionManager()

    @Provides
    @Singleton
    fun provideSettingManager() = SettingRepo()
}