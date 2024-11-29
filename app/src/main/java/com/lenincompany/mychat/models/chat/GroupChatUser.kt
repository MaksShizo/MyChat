package com.lenincompany.mychat.models.chat

import com.google.gson.annotations.SerializedName

data class GroupChatUser (
    /**
     * Id участника.
     */
    @SerializedName("UserId")
    val userId: Int,

    /**
     * Имя участника.
     */
    @SerializedName("UserName")
    val userName: String
)