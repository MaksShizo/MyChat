package com.lenincompany.mychat.ui.chats

import com.lenincompany.mychat.models.ChatBody
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ChatsView : MvpView {

    fun showChats(chatResponse: List<ChatBody>)
}