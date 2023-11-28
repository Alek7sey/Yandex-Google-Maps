package ru.netology.yandexmaps.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.yandexmaps.dto.PlaceMark

interface PlaceMarkRepository {
    val data: Flow<List<PlaceMark>>
    suspend fun removeById(id: Long)
    suspend fun addPlaceMark(placeMark: PlaceMark)
    fun updateById(placeMark: PlaceMark)
}