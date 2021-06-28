package com.canlioya.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NewsEntity::class],
    version = 3, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {

        private const val DATABASE_NAME = "news_reader"
        @Volatile private var sInstance: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase {
            return sInstance ?: synchronized(this) {
                sInstance ?: Room.databaseBuilder(context.applicationContext,
                    NewsDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { sInstance = it }
            }
        }
    }
}