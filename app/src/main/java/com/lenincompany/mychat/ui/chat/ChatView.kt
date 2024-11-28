package com.lenincompany.mychat.ui.chat

import com.lenincompany.mychat.models.Message
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface ChatView: MvpView {

    fun showMessage(messageResponse: List<Message>)
}