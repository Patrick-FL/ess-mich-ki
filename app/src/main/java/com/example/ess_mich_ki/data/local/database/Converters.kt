package com.example.ess_mich_ki.data.local.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toDoubleList(value: String?): List<Double>? {
        return value?.split(",")?.mapNotNull { it.toDoubleOrNull() }
    }
}
