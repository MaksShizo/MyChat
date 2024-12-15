package com.lenincompany.mychat.data

import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.base.MessageServer
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.chat.ChatInfo
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.lenincompany.mychat.models.user.UserResponse
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.network.ApiService
import com.lenincompany.mychat.network.ApiServiceScalar
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class DataRepository @Inject constructor(
    val apiService: ApiService,
    val apiServiceScalar: ApiServiceScalar
) {
    // Auth

    suspend fun register(user: UserResponse): Response<Void> {
        return apiService.register(user)
    }

    suspend fun login(loginRequest: LoginRequest): Response<Token> {
        return apiService.login(loginRequest)
    }

    suspend fun resetPassword(email: String): Response<Void> {
        return apiService.resetPassword(email)
    }

    suspend fun refresh(token: Token): Response<Token> {
        return apiService.refresh(token)
    }

    // Chats

    suspend fun getChats(userId: Int): Response<List<ChatBody>> {
        return apiService.getChats(userId)
    }

    suspend fun getMessagesInChat(chatId: Int): Response<List<Message>> {
        return apiService.getMessages(chatId)
    }

    suspend fun getUsersInChat(chatId: Int): Response<List<ChatUsers>> {
        return apiService.getUsersInChat(chatId)
    }

    suspend fun addUserInChat(chatId: Int, userId: Int): Response<Boolean> {
        return apiService.addUserInChat(chatId, userId)
    }

    suspend fun addUsersInChat(chatId: Int, userId: List<Int>): Response<Boolean> {
        return apiService.addUsersInChat(chatId, userId)
    }

    suspend fun downloadChatPhoto(chatId: Int): Response<ResponseBody> {
        return apiServiceScalar.getGroupChatPhoto(chatId)
    }

    suspend fun getGroupChatInfo(chatId: Int): Response<ChatInfo> {
        return apiService.getGroupChatInfo(chatId)
    }

    suspend fun uploadChatPhoto(chatId: Int, file: MultipartBody.Part): MessageServer {
        return apiService.uploadChatPhoto(chatId, file)
    }



    // User

    suspend fun uploadUserPhoto(userId: Int, file: MultipartBody.Part): MessageServer {
        return apiService.uploadPhoto(userId, file)
    }

    suspend fun getUser(userId: Int):  Response<UserInfoResponse> {
        return apiService.getUser(userId)
    }

    suspend fun downloadUserPhoto(userId: Int): Response<ResponseBody> {
        return apiServiceScalar.downloadPhoto(userId)
    }
}