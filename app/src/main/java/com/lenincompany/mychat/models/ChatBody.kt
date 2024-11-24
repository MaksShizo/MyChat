package com.lenincompany.mychat.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import java.time.LocalDate

data class ChatBody (
    /**
     * Id чата.
     */
    @SerializedName("ChatId")
    val chatId: Int,

    /**
     * Название чата.
     */
    @SerializedName("Name")
    val name: String,

    /**
     * Пользователь, создавший чат.
     */
    @SerializedName("UserId")
    val userId: Int,

    /**
     * Дата создания чата.
     */
    @SerializedName("CreateDate")
    val createDate: String,

    /**
     * Участники чата.
     */
    @SerializedName("GroupChatUsers")
    val groupChatUsers: List<GroupChatUser> = emptyList()
)
