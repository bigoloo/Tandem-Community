package com.iamamin.tandemcommunity.data.mapper

import com.iamamin.tandemcommunity.data.remote.model.UserDto
import com.iamamin.tandemcommunity.domain.model.CommunityUser


fun UserDto.toCommunityUser(): CommunityUser {

    return CommunityUser(
        id = id,
        firstname = firstname,
        topic = topic,
        pictureUrl = pictureUrl,
        natives = natives,
        learns = learns,
        referenceCnt = referenceCnt
    )
}