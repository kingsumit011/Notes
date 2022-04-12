package com.example.notes

import androidx.lifecycle.ViewModel
import com.example.notes.data.DataManager
import com.example.notes.data.local.AppDatabase
import com.example.notes.data.local.model.NotesModel
import com.example.notes.data.local.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainVeiwModel @Inject constructor(
    private var mAppDatabase: AppDatabase
) : ViewModel() {

        fun getNotes() = mAppDatabase.noteDao().getNotes()
        fun inserNotes(note:NotesModel) = mAppDatabase.noteDao().insert(note)
        fun updateNote(note:NotesModel) = mAppDatabase.noteDao().update(note)
        fun getNoteById(id:Int) = mAppDatabase.noteDao().getSelectedNotesbyid(id)
        fun UserExist(email:String?=null , phone:String? =null)=
            mAppDatabase.userDao().getSelectedUser(email , phone).isNotEmpty()
        fun register(user: User) = mAppDatabase.userDao().insert(user)
        fun checkPassword(email:String?=null, phone: String? =null, password:String)= mAppDatabase.userDao().checkPassword(email , phone , password).isNotEmpty()
}

