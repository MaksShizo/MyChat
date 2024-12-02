package com.lenincompany.mychat.ui.main.settings

import com.lenincompany.mychat.models.user.UserInfoResponse
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.io.InputStream

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : MvpView {

    fun setInfoOnActivity(response: UserInfoResponse)

    fun setPhoto(inputStream: InputStream)
}