package com.lenincompany.mychat.data

import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.Message
import com.lenincompany.mychat.models.MessageServer
import com.lenincompany.mychat.models.Token
import com.lenincompany.mychat.models.UserInfoResponse
import com.lenincompany.mychat.models.UserResponse
import com.lenincompany.mychat.network.ApiService
import com.lenincompany.mychat.network.ApiServiceScalar
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class DataRepository @Inject constructor(
    val apiService: ApiService,
    val apiServiceScalar: ApiServiceScalar
) {
    fun getChats(userId: Int): Single<Response<List<ChatBody>>> {
        return apiService.getChats(userId)
    }

    fun getMessages(chatId: Int): Single<Response<List<Message>>> {
        return apiService.getMessages(chatId)
    }

    fun getUser(userId: Int): Single<Response<UserInfoResponse>> {
        return apiService.getUser(userId)
    }

    fun register(user: UserResponse): Single<Response<Void>> {
        return apiService.register(user)
    }

    fun login(loginRequest: LoginRequest): Single<Response<Token>> {
        return apiService.login(loginRequest)
    }

    fun resetPassword(email: String): Single<Response<Void>> {
        return apiService.resetPassword(email)
    }

    fun refresh(token: Token): Single<Response<Token>> {
        return apiService.refresh(token)
    }

    fun uploadPhoto(userId: Int, file: MultipartBody.Part): Single<MessageServer> {
        return apiService.uploadPhoto(userId, file)
    }

    fun downloadPhoto(userId: Int): Single<Response<ResponseBody>> {
        return apiServiceScalar.downloadPhoto(userId)
    }
}