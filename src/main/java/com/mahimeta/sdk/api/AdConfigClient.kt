package com.mahimeta.sdk.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AdConfigClient private constructor() {
    private val retrofit: Retrofit
    val service: AdConfigService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://mahimeta.com/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(AdConfigService::class.java)
    }

    companion object {
        private var instance: AdConfigClient? = null
        
        fun getInstance(): AdConfigClient {
            return instance ?: synchronized(this) {
                instance ?: AdConfigClient().also { instance = it }
            }
        }
    }
}
