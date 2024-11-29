package com.lenincompany.mychat.ui.main.chats

import com.lenincompany.mychat.models.chat.ChatBody
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ChatsView : MvpView {

    fun showChats(chatResponse: List<ChatBody>)
}