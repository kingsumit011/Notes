package com.example.notes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notes.data.local.model.NotesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao : BaseDao<NotesModel> {

    @Query("SELECT * FROM notes_table")
    fun getNotes(): List<NotesModel>

    @Query("SELECT * FROM notes_table where author = :author")
    fun getSelectedNotes(author:String): List<NotesModel>

    @Query("SELECT * FROM notes_table where _id =:id")
    fun getSelectedNotesbyid(id:Int): NotesModel?

    @Insert
    override fun insert(vararg note: NotesModel)

    @Insert
    fun insetAll(notes: List<NotesModel>)

    @Update
    fun update(note: NotesModel)
}