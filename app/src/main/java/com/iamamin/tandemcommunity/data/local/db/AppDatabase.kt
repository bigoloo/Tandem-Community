package com.iamamin.tandemcommunity.data.local.db

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.iamamin.tandemcommunity.data.local.dao.LikedUserDao
import com.iamamin.tandemcommunity.data.local.entity.LikedUserEntity

@Database(entities = [LikedUserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun likedUserDao(): LikedUserDao
}
