package com.lenincompany.mychat.network

import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.models.Message
import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.MessageServer
import com.lenincompany.mychat.models.Token
import com.lenincompany.mychat.models.UserInfoResponse
import com.lenincompany.mychat.models.UserResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming
import java.io.File

interface ApiService {

    @GET("api/GroupChat/user/{userId}")
    fun getChats(@Path("userId") id: Int): Single<Response<List<ChatBody>>>

    @GET("api/GroupChat/chat/messages/{chatId}")
    fun getMessages(@Path("chatId") id: Int): Single<Response<List<Message>>>

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