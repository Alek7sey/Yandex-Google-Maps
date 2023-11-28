package ru.netology.yandexmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.yandexmaps.dto.PlaceMark

@Entity
data class PlaceMarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val longitude: Double,
    val latitude: Double,
) {
    fun toDto() =
        PlaceMark(
            id = id,
            title = title,
            longitude = longitude,
            latitude = latitude,
        )

    companion object {
        fun fromDto(dto: PlaceMark) =
            PlaceMarkEntity(
                id = dto.id,
                title = dto.title,
                longitude = dto.longitude,
                latitude = dto.latitude,
            )
    }
}

fun List<PlaceMarkEntity>.toDto(): List<PlaceMark> = map(PlaceMarkEntity::toDto)
fun List<PlaceMark>.toEntity(): List<PlaceMarkEntity> = map(PlaceMarkEntity::fromDto)