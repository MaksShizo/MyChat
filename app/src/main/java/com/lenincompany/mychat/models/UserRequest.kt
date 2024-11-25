package com.lenincompany.mychat.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserRequest (
    /**
     * Id сообщения
     */
    val Name: String? = null,

    /**
     * Id чата
     */
    val Email: String,

    /**
     * Id пользователя, отправившего сообщение
     */
    val Password: String,
)