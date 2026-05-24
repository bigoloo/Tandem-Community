package com.iamamin.tandemcommunity.data.remote

import com.iamamin.tandemcommunity.data.remote.model.CommunityResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CommunityApi {

    @GET("community_{pageNumber}.json")
    suspend fun getCommunity(@Path("pageNumber") pageNumber: Int): CommunityResponse
}
