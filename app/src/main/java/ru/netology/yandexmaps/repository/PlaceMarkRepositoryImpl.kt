package ru.netology.yandexmaps.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.yandexmaps.dao.PlaceMarkDao
import ru.netology.yandexmaps.dto.PlaceMark
import ru.netology.yandexmaps.entity.PlaceMarkEntity

class PlaceMarkRepositoryImpl(
    private val dao: PlaceMarkDao
) : PlaceMarkRepository {

    override val data: Flow<List<PlaceMark>> = dao.getAll().map {
        it.map(PlaceMarkEntity::toDto)
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }

    override suspend fun addPlaceMark(placeMark: PlaceMark) {
        dao.insert(PlaceMarkEntity.fromDto(placeMark))
    }

    override fun updateById(placeMark: PlaceMark) {
        dao.updateById(placeMark.id, placeMark.title)
    }
}