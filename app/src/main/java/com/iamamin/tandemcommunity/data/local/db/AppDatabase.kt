package com.iamamin.tandemcommunity.data.local.db

import androidx.room3.RoomDatabase
import com.iamamin.tandemcommunity.data.local.dao.LikedUserDao

abstract class AppDatabase : RoomDatabase() {
    abstract fun likedUserDao(): LikedUserDao
}