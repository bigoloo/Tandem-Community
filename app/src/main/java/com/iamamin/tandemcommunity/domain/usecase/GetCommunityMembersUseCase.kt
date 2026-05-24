package com.iamamin.tandemcommunity.domain.usecase

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.iamamin.tandemcommunity.domain.model.CommunityMember
import com.iamamin.tandemcommunity.domain.repository.CommunityRepository
import com.iamamin.tandemcommunity.domain.repository.LikedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetCommunityMembersUseCase(
    private val communityRepository: CommunityRepository,
    private val likedRepository: LikedRepository,
) {
    operator fun invoke(scope: CoroutineScope): Flow<PagingData<CommunityMember>> {
        return communityRepository
            .getCommunityUsers()
            .cachedIn(scope)
            .combine(likedRepository.observeLikedUserIds()) { pagingData, likedIds ->
                pagingData.map { user ->
                    CommunityMember(
                        id = user.id,
                        firstname = user.firstname,
                        topic = user.topic,
                        pictureUrl = user.pictureUrl,
                        native = user.natives.first(),
                        learn = user.learns.first(),
                        isNew = user.referenceCnt == 0,
                        isLiked = user.id in likedIds
                    )
                }
            }
    }
}
