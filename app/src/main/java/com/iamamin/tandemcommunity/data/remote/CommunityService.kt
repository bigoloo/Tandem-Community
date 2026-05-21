package com.iamamin.tandemcommunity.data.remote

import com.iamamin.tandemcommunity.data.remote.model.CommunityResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CommunityService {

    @GET("community_{pageNumber}.json")
    suspend fun getCommunities(@Path("pageNumber") pageNumber: Int): CommunityResponse
}