package com.lenincompany.mychat.ui.chat

import android.util.Log
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.ui.main.chats.ChatsView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ChatPresenter @Inject constructor(
    private val dataRepository: DataRepository
) : MvpPresenter<ChatView>() {
    private var call: Disposable? = null

    fun getMessages(chatId: Int)
    {
        call = dataRepository.getMessages(chatId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messageResponse ->
                    if(messageResponse.isSuccessful)
                    {
                        viewState.showMessage(messageResponse.body()!!)
                    } else {
                        Log.e("ChatsPresenter Error", messageResponse.message())
                    }
                },
                { throwable ->
                    // Обрабатываем ошибку, например, сетевую ошибку
                    Log.e("ApiError", "Error occurred: ${throwable.message}")
                }
            )
    }

}