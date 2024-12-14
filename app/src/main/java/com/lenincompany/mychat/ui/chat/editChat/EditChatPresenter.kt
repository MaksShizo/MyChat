package com.lenincompany.mychat.ui.chat.editChat

import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.ui.chat.ChatView
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import javax.inject.Inject

class EditChatPresenter @Inject constructor(
    private val dataRepository: DataRepository
) : MvpPresenter<ChatView>() {
    private var call: Disposable? = null
}