package com.iamamin.tandemcommunity.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "liked_users")
data class LikedUserEntity(
    @PrimaryKey
    val userId: Long
)