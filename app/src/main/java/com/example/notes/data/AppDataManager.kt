package com.justicepoker.data

import com.example.notes.data.local.AppDatabase
import com.example.notes.data.DataManager
import javax.inject.Inject

class AppDataManager @Inject constructor(

    private val mAppDatabase: AppDatabase
) : DataManager {

    override fun getAppDataBase() = mAppDatabase


    override fun clearDataBase() {
        //mAppDatabase.noteDao().deleteAllUsers()

    }

}
