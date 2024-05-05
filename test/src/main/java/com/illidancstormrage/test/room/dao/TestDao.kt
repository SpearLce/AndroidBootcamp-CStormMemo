package com.illidancstormrage.test.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Update

@Dao
interface TestDao {

    @Insert
    fun insertUser(user: User)
    @Update
    fun update(user: User)
}

@Entity(tableName = "User")
data class User(
    var name: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)
