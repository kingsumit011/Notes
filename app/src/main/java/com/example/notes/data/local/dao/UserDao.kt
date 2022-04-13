package com.example.notes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notes.data.local.model.NotesModel
import com.example.notes.data.local.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM user")
    fun getUser(): List<User>

    @Query("SELECT * FROM user where email = :email OR phoneNo =:phoneNo" )
    fun getSelectedUser(email:String? = "", phoneNo:String? =null): List<User>
    @Query("SELECT * FROM user where (email = :email OR phoneNo =:phoneNo) AND password =:password" )
    fun checkPassword(email:String? = "", phoneNo:String ? =null , password:String): List<User>
    @Query("SELECT * FROM user where _id =:id")
    fun getSelectedUserbyid(id:Int): User?
    @Insert
    override fun insert(vararg user: User)
    @Insert
    fun insetAll(notes: List<User>)
    @Update
    fun update(note: User)
}