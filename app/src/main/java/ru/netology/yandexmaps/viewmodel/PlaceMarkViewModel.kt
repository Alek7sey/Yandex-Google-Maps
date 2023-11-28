package ru.netology.yandexmaps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.db.PlaceMarkDb
import ru.netology.yandexmaps.dto.PlaceMark
import ru.netology.yandexmaps.entity.PlaceMarkEntity
import ru.netology.yandexmaps.repository.PlaceMarkRepository
import ru.netology.yandexmaps.repository.PlaceMarkRepositoryImpl

class PlaceMarkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlaceMarkRepository = PlaceMarkRepositoryImpl(PlaceMarkDb.getInstance(application).placeMarkDao())
    val data = PlaceMarkDb.getInstance(application).placeMarkDao()
    val placemarks = data.getAll().map {
        it.map(PlaceMarkEntity::toDto)
    }

    fun addPlaceMark(placeMark: PlaceMark) {
        viewModelScope.launch {
            repository.addPlaceMark(placeMark)
        }
    }

    fun removePlaceMark(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }
}