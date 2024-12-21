package com.lenincompany.mychat.network

import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.base.MessageServer
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.models.chat.ChatInfo
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.lenincompany.mychat.models.user.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Auth

    @POST("api/Auth/register")
    suspend fun register(@Body user: UserResponse): Response<Void>

    @POST("api/Auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Token>

    @POST("api/Auth/resetPassword")
    suspend fun resetPassword(@Query("email") email: String): Response<Void>

    @POST("api/Auth/refresh")
    suspend fun refresh(@Body token: Token): Response<Token>

    // User

    @Multipart
    @POST("api/User/uploadUserPhoto/{userId}")
    suspend fun uploadPhoto(@Path("userId") userId: Int, @Part file: MultipartBody.Part): MessageServer

    @GET("api/User/user/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): Response<UserInfoResponse>

    @POST("api/User/user/phone")
    suspend fun getUserForPhone(@Body phones: List<String>): Response<List<UserInfoResponse>>
    // GroupChat

    @GET("api/GroupChat/user/{userId}")
    suspend fun getChats(@Path("userId") id: Int): Response<List<ChatBody>>

    @GET("api/GroupChat/chat/messages/{chatId}")
    suspend fun getMessages(@Path("chatId") id: Int): Response<List<Message>>

    @GET("api/GroupChat/chat/users/{chatId}")
    suspend fun getUsersInChat(@Path("chatId") id: Int): Response<List<ChatUsers>>

    @POST("api/GroupChat/addUser/{chatId}/{userId}")
    suspend fun addUserInChat(@Path("chatId") chatId: Int, @Path("userId") userId: Int): Response<Boolean>

    @POST("api/GroupChat/addUsers/{chatId}")
    suspend fun addUsersInChat(@Path("chatId") chatId: Int, @Body userId: List<Int>): Response<Boolean>

    @GET("api/GroupChat/chat/{chatId}")
    suspend fun getGroupChatInfo(@Path("chatId") chatId: Int): Response<ChatInfo>

    @Multipart
    @POST("api/GroupChat/uploadChatPhoto/{chatId}")
    suspend fun uploadChatPhoto(@Path("chatId") chatId: Int, @Part file: MultipartBody.Part): MessageServer

    @Multipart
    @POST("api/GroupChat/uploadChatFiles/{chatId}")
    suspend fun uploadChatFile(@Path("chatId") chatId: Int, @Part file: MultipartBody.Part): MessageServer

}