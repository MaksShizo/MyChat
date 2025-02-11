package com.lenincompany.mychat.models.chat

import com.google.gson.annotations.SerializedName

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


    @SerializedName("Photo")
    val photo: String?,

    /**
     * Участники чата.
     */
    @SerializedName("GroupChatUsers")
    val groupChatUsers: List<GroupChatUser> = emptyList(),

    /**
     * Последнее сообщение
     */
    @SerializedName("LastMessage")
    val lastMessage: Message
)
