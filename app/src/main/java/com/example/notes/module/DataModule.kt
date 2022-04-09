package com.example.notes.module

import android.content.Context
import com.example.notes.data.local.AppDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

//    @Provides
//    @Singleton
//    fun provideUserDao(appDatabase: AppDatabase): UserDao {
//        return appDatabase.userDao()
//    }

}