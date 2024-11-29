package com.lenincompany.mychat.ui.main.settings

import android.graphics.Bitmap
import com.lenincompany.mychat.models.UserInfoResponse
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.io.File
import java.io.InputStream

@StateStrategyType(AddToEndSingleStrategy::class)
interface SettingsView : MvpView {

    fun setInfoOnActivity(response: UserInfoResponse)

    fun setPhoto(inputStream: InputStream)
}