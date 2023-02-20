package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mshdabiola.database.model.DrawPathEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PathDao {
    @Query("SELECT * FROM path_table WHERE imageId=:imageID ORDER BY pathId")
    fun getPaths(imageID: Long): Flow<List<DrawPathEntity>>

    @Query("DELETE FROM PATH_TABLE WHERE imageId=:imageID")
    fun delete(imageID: Long)

    @Insert
    fun insert(list: List<DrawPathEntity>)
}
