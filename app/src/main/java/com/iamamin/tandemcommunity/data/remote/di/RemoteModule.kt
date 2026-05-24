package com.iamamin.tandemcommunity.data.remote.di

import com.iamamin.tandemcommunity.data.remote.CommunityApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT_SECONDS = 3L
private const val WRITE_TIMEOUT_SECONDS = 25L

val remoteModule = module {

    single {

        val intercepter = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().apply {
            this.addInterceptor(intercepter)
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        }.build()
        val networkJson = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("https://tandem2019.web.app/api/")
            .addConverterFactory(networkJson.asConverterFactory(contentType))
            .client(client)
            .build()
    }

    single {
        get<Retrofit>().create(CommunityApi::class.java)
    }
}
