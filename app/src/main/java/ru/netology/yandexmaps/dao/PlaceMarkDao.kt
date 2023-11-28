package ru.netology.yandexmaps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.yandexmaps.entity.PlaceMarkEntity

@Dao
interface PlaceMarkDao {
    @Query("SELECT * FROM PlaceMarkEntity")
    fun getAll(): Flow<List<PlaceMarkEntity>>

    @Query("DELETE FROM PlaceMarkEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PlaceMarkEntity SET title = :title WHERE id = :id")
    fun updateById(id: Long, title: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(placeMark: PlaceMarkEntity)

}