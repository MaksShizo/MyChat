package com.lenincompany.mychat.ui.chat

import com.lenincompany.mychat.models.Message
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface ChatView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showMessage(messageResponse: List<Message>)
}