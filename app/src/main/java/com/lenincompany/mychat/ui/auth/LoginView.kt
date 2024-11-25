package com.lenincompany.mychat.ui.auth

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType



interface LoginView: MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setupTokenRefresher()
}