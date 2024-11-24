package com.lenincompany.mychat.network

import com.lenincompany.mychat.models.ChatBody
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/GroupChat/user/{userId}")
    fun getChats(@Path("userId") id: Int): Single<Response<List<ChatBody>>>
}