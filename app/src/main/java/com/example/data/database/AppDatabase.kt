package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.UserDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.SubcategoryDao
import com.example.data.dao.ServiceRequestDao
import com.example.data.dao.ChatMessageDao
import com.example.data.model.User
import com.example.data.model.Category
import com.example.data.model.Subcategory
import com.example.data.model.ServiceRequest
import com.example.data.model.ChatMessage

@Database(
    entities = [
        User::class,
        Category::class,
        Subcategory::class,
        ServiceRequest::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun serviceRequestDao(): ServiceRequestDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hommysist_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
