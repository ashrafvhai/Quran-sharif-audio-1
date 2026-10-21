package com.example.data.local

import android.content.Context

class QuranDatabase(private val dao: QuranDao) {
    fun quranDao(): QuranDao = dao

    companion object {
        @Volatile
        private var INSTANCE: QuranDatabase? = null

        fun getDatabase(context: Context): QuranDatabase {
            return INSTANCE ?: synchronized(this) {
                val dao = SQLiteQuranDao(context.applicationContext)
                val instance = QuranDatabase(dao)
                INSTANCE = instance
                instance
            }
        }
    }
}
