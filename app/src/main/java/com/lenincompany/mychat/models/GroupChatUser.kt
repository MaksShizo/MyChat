package com.lenincompany.mychat.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

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