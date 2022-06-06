package com.aditasha.rawgio.core.data.local.database

import androidx.room.TypeConverter

class DataConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromMutableList(list: List<String>): String =
            list.joinToString(separator = " ") { it }

        @TypeConverter
        @JvmStatic
        fun toMutableList(string: String): MutableList<String> {
            val data = string.split(" ")
            val mutable = mutableListOf<String>()
            mutable.addAll(data)
            return mutable
        }
    }
}