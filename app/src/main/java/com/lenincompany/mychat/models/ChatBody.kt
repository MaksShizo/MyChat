package com.lenincompany.mychat.models

import java.time.LocalDate

data class ChatBody (
    /**
     * Id чата.
     */
    val chatId: Int,

    /**
     * Название чата.
     */
    val name: String,

    /**
     * Пользователь, создавший чат.
     */
    val userId: Int,

    /**
     * Дата создания чата.
     */
    val createDate: LocalDate,

    /**
     * Участники чата.
     */
    val groupChatUsers: List<GroupChatUser> = emptyList()
)
