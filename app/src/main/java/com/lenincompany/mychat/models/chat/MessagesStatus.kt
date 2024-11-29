package com.lenincompany.mychat.models.chat

import com.google.gson.annotations.SerializedName

data class MessagesStatus(
    /**
     * Id сообщения
     */
    @SerializedName("ChatId")
    val messageId: Int,

    /**
     * Прочитано?
     */
    @SerializedName("ChatId")
    val isRead: Boolean,

    /**
     * Id пользователя, прочитавшего
     */
    @SerializedName("UserId")
    val userId: Int,

    /**
     * Сообщение
     */
    @SerializedName("Message")
    val message: Message
)