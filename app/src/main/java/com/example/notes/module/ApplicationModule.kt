package com.example.notes.module

import com.example.notes.data.DataManager
import com.example.notes.data.local.AppDatabase
import com.justicepoker.data.AppDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {


    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideDataManager(

        appDatabase: AppDatabase
    ): DataManager = AppDataManager(appDatabase)


}