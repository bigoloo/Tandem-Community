
package com.iamamin.tandemcommunity.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.iamamin.tandemcommunity.data.paging.CommunityPagingSource
import com.iamamin.tandemcommunity.data.remote.CommunityApi
import com.iamamin.tandemcommunity.domain.model.CommunityUser
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class CommunityRepositoryImpl(
    private val communityApi: CommunityApi
) : CommunityRepository {

    override fun getCommunityUsers(): Flow<PagingData<CommunityUser>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommunityPagingSource(api = communityApi) }
        ).flow
    }
}