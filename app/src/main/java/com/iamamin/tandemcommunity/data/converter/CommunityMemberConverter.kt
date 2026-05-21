package com.iamamin.tandemcommunity.data.converter

import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.domain.model.CommunityMember


fun UserDto.toDomainEntity(isLiked: Boolean): CommunityMember {

    return CommunityMember(
        id = id,
        firstname = firstname,
        topic = topic,
        pictureUrl = pictureUrl,
        natives = natives,
        learns = learns,
        referenceCnt = referenceCnt,
        isNew = referenceCnt == 0,
        isLiked = isLiked

    )
}