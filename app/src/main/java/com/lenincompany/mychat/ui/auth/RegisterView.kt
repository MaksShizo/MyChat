package com.lenincompany.mychat.ui.auth

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface RegisterView: MvpView {

    fun confirmRegister()
}