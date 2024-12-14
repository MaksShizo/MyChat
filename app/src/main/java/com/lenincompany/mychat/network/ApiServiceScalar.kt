package com.lenincompany.mychat.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiServiceScalar {
    @GET("api/User/downloadUserPhoto/{userId}")
    @Streaming
    suspend fun downloadPhoto(@Path("userId") userId: Int): Response<ResponseBody>
}