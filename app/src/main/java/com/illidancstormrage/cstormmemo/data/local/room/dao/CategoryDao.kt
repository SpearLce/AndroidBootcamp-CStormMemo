package com.illidancstormrage.cstormmemo.data.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.illidancstormrage.cstormmemo.model.category.Category
import com.illidancstormrage.cstormmemo.model.category.CategoryWithMemos
import com.illidancstormrage.utils.database.room.dao.BaseDao
@Dao
abstract class CategoryDao : BaseDao<Category>() {

    @Transaction
    @Query("select * from Category where id = :id")
    abstract fun selectCategoryWithMemos(id: Long): CategoryWithMemos
}