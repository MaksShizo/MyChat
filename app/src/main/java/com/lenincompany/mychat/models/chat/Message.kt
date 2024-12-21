package com.lenincompany.mychat.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    /**
     * Id сообщения
     */
    val MessageId: Int? = null,

    /**
     * Id чата
     */
    val ChatId: Int,

    /**
     * Id пользователя, отправившего сообщение
     */
    val UserId: Int,

    /**
     * Содержание
     */
    val Content: String,

    /**
     * Дата отправки сообщения
     */
    val DateCreate: String,

    /**
     * Дата отправки сообщения
     */
    val Type: Short,

    /**
     * Статус сообщения (прочитано или нет)
     */

    // @SerializedName("MessageStatus") val messagesStatus: MessagesStatus? = null
)
{
    companion object {
        const val TEXT : Short = 1
        const val IMAGE : Short = 2
        const val VIDEO : Short = 3
        const val DOC : Short = 4
        const val ERROR : Short = 0
    }
}

