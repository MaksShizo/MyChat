package com.lenincompany.mychat.data

import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.network.ApiService
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import javax.inject.Inject

class DataRepository @Inject constructor(
    val apiService: ApiService
) {
    fun getChats(userId: Int) : Single<Response<List<ChatBody>>>
    {
        return apiService.getChats(userId)
    }
}