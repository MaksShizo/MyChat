package com.lenincompany.mychat.ui.auth

import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.user.UserInfoResponse
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType


@StateStrategyType(AddToEndSingleStrategy::class)
interface LoginView: MvpView {

    fun saveUserInfo(userInfoResponse: UserInfoResponse)

    fun setupTokenRefresher(token : Token)
}