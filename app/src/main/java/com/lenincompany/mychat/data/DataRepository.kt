package com.lenincompany.mychat.data

import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.Message
import com.lenincompany.mychat.models.Token
import com.lenincompany.mychat.models.UserRequest
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

    fun getMessages(chatId: Int) : Single<Response<List<Message>>>
    {
        return apiService.getMessages(chatId)
    }

    fun register(user: UserRequest): Single<Response<Void>> {
        return apiService.register(user)
    }

    fun login(loginRequest : LoginRequest): Single<Response<Token>> {
        return apiService.login(loginRequest)
    }

    fun resetPassword(email: String): Single<Response<Void>> {
        return apiService.resetPassword(email)
    }

    fun refresh(token: Token): Single<Response<Token>> {
        return apiService.refresh(token)
    }
}