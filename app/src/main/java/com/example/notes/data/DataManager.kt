package com.example.notes.data

import com.example.notes.data.local.AppDatabase
import com.example.notes.data.local.DbHelper

interface DataManager: DbHelper {

    fun getAppDataBase(): AppDatabase
}