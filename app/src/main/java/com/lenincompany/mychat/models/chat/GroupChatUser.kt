package com.lenincompany.mychat.models.chat

import com.google.gson.annotations.SerializedName
import com.lenincompany.mychat.models.user.UserInfoResponse

data class GroupChatUser (
    /**
     * Id участника.
     */
    @SerializedName("UserId")
    val userId: Int,
    /**
     * Все данные пользователя.
     */
    @SerializedName("User")
    val user: UserInfoResponse
)