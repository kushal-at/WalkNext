package com.walknxt.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.walknxt.app.data.local.dao.RoutePointDao
import com.walknxt.app.data.local.dao.SessionDao
import com.walknxt.app.data.local.entity.RoutePointEntity
import com.walknxt.app.data.local.entity.WalkSessionEntity

@Database(entities = [WalkSessionEntity::class, RoutePointEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun routePointDao(): RoutePointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "walknxt_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
