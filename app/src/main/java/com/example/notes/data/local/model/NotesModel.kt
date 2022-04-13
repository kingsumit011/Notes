package com.example.notes.data.local.model


import androidx.room.*
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*

@Entity(tableName = "notes_table")
data class NotesModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = null,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description : String,

    @ColumnInfo(name = "image" )
    var image: List<String?>,

    @ColumnInfo(name = "time")
    var time: Date? = null,

    @ColumnInfo(name = "author")
    var author :String? = null,


    ) {
    override fun toString(): String {
        return "NotesModel(id=$id, title='$title', themeName='$description', image='$image', time=$time, author='$author')"
    }

    class Converters {

        @TypeConverter
        fun listToJson(value: List<String>?) = Gson().toJson(value)

        @TypeConverter
        fun jsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
    }
}
