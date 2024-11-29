package com.lenincompany.mychat.network

import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.io.File

interface ApiServiceScalar {
    @GET("api/User/downloadUserPhoto/{userId}")
    @Streaming
    fun downloadPhoto(@Path("userId") userId: Int): Single<Response<ResponseBody>>
}