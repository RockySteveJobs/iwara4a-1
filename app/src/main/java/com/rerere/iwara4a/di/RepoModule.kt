package com.rerere.iwara4a.di

import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.backend.Iwara4aBackendAPI
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    @Singleton
    fun provideMediaRepo(
        iwaraApi: IwaraApi,
        iwara4aBackendAPI: Iwara4aBackendAPI
    ) = MediaRepo(iwaraApi, iwara4aBackendAPI)

    @Provides
    @Singleton
    fun provideUserRepo(
        iwaraApi: IwaraApi
    ) = UserRepo(iwaraApi)
}