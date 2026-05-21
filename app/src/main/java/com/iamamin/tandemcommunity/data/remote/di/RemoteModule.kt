package com.iamamin.tandemcommunity.data.remote.di

import com.iamamin.tandemcommunity.data.remote.CommunityService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val remoteModule = module {

    single {

        val networkJson = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://tandem2019.web.app/api/")
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .build()
    }

    single {
        get<Retrofit>().create(CommunityService::class.java)
    }
}