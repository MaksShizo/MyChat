package com.lenincompany.mychat.models.chat

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    /**
     * Id сообщения
     */
    @SerializedName("MessageId")
    val MessageId: Int? = null,

    /**
     * Id чата
     */
    @SerializedName("ChatId")
    val ChatId: Int,

    /**
     * Id пользователя, отправившего сообщение
     */
    @SerializedName("UserId")
    val UserId: Int,

    /**
     * Содержание
     */
    @SerializedName("Content")
    val Content: String,

    /**
     * Дата отправки сообщения
     */
    @SerializedName("DateCreate")
    val DateCreate: String,

    /**
     * Статус сообщения (прочитано или нет)
     */

    // @SerializedName("MessageStatus") val messagesStatus: MessagesStatus? = null
)