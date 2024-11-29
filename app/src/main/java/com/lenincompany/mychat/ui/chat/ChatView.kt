package com.lenincompany.mychat.ui.chat

import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.io.InputStream

@StateStrategyType(AddToEndSingleStrategy::class)
interface ChatView: MvpView {

    fun showMessage(messageResponse: List<Message>)

    fun setUser(usersResponse: List<ChatUsers>)

    fun savePhoto(inputStream: InputStream, userId: Int)
}