package com.example.notes.data.local.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "time")
    var time: Date? = null,

    @ColumnInfo(name = "author")
    var author :String? = null,


    ) {
    override fun toString(): String {
        return "NotesModel(id=$id, title='$title', themeName='$description', image='$image', time=$time, author='$author')"
    }
}
