package com.lenincompany.mychat.models

data class GroupChatUser (
    /**
     * Id участника.
     */
    val userId: Int,

    /**
     * Имя участника.
     */
    val userName: String
)