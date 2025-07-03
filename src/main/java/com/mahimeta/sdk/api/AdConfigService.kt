package com.mahimeta.sdk.api

import com.mahimeta.sdk.model.AdConfigResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AdConfigService {
    @Headers("Content-Type: application/json")
    @GET("ad_serve.php")
    suspend fun getAdConfig(
        @Query("publisher_id") publisherId: String
    ): AdConfigResponse
}
