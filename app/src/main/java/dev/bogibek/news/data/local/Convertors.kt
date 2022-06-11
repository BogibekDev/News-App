package dev.bogibek.news.data.local

import androidx.room.TypeConverter
import dev.bogibek.news.model.Source

class Convertors {
    @TypeConverter
    fun fromSource(source: Source) = source.name

    @TypeConverter
    fun toSource(name: String) = Source(name, name)
}