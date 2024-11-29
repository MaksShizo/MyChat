package com.lenincompany.mychat.network

import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.base.MessageServer
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.UserInfoResponse
import com.lenincompany.mychat.models.UserResponse
import com.lenincompany.mychat.models.chat.ChatUsers
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("api/GroupChat/user/{userId}")
    fun getChats(@Path("userId") id: Int): Single<Response<List<ChatBody>>>

    @GET("api/GroupChat/chat/messages/{chatId}")
    fun getMessages(@Path("chatId") id: Int): Single<Response<List<Message>>>

    @GET("api/GroupChat/chat/users/{chatId}")
    fun getUsersInChat(@Path("chatId") id: Int): Single<Response<List<ChatUsers>>>

    @GET("api/User/user/{userId}")
    fun getUser(@Path("userId") userId: Int): Single<Response<UserInfoResponse>>

    @POST("api/Auth/register")
    fun register(@Body user: UserResponse): Single<Response<Void>>

    @POST("api/Auth/login")
    fun login(@Body loginRequest : LoginRequest): Single<Response<Token>>

    @POST("api/Auth/resetPassword")
    fun resetPassword(email: String): Single<Response<Void>>

    @POST("api/Auth/refresh")
    fun refresh(@Body token: Token): Single<Response<Token>>

    @Multipart
    @POST("api/User/uploadUserPhoto/{userId}")
    fun uploadPhoto(@Path("userId") userId: Int, @Part file: MultipartBody.Part): Single<MessageServer>
}