package com.iamamin.tandemcommunity.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.iamamin.tandemcommunity.data.local.entity.LikedUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedUserDao {
    @Query("SELECT userId FROM liked_users")
    fun observeAll(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun like(entity: LikedUserEntity)

    @Query("DELETE FROM liked_users WHERE userId = :userId")
    suspend fun unlike(userId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM liked_users WHERE userId = :userId)")
    suspend fun isLiked(userId: Long): Boolean
}
