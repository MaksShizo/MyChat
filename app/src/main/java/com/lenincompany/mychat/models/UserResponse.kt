package com.lenincompany.mychat.models

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse (
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