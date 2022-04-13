package com.example.notes.data.local

import android.content.Context
import androidx.room.*
import com.example.notes.data.local.dao.NotesDao
import com.example.notes.data.local.dao.UserDao
import com.example.notes.data.local.model.NotesModel
import com.example.notes.data.local.model.User
import com.example.notes.utils.Constant
import com.google.gson.Gson
import java.util.*

val TAG = "Db"

@Database(
    entities = [
        NotesModel::class,
        User::class
    ],
    version = 1,
)
@TypeConverters(Converters::class  )
abstract class AppDatabase : RoomDatabase() {


    abstract fun noteDao(): NotesDao
    abstract fun userDao(): UserDao


    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constant.DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}
