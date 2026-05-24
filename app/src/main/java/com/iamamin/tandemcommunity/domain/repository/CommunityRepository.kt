package com.iamamin.tandemcommunity.domain.repository

import androidx.paging.PagingData
import com.iamamin.tandemcommunity.domain.model.CommunityUser
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun getCommunityUsers(): Flow<PagingData<CommunityUser>>
}
