package com.example.notes.data.local.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user")
data class User(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = null,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "email")
    var email: String? = null,

    @ColumnInfo(name = "phoneNo")
    var phoneNo: String? = null,

    @ColumnInfo(name = "time")
    var time: Date?=null,

    @ColumnInfo(name = "password")
    var password:String,


    ) {

}
