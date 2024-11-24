package com.lenincompany.mychat.ui.chats

import android.util.Log
import com.lenincompany.mychat.data.DataRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import javax.inject.Inject

@InjectViewState
class ChatsPresenter @Inject constructor(
    private val dataRepository: DataRepository
) : MvpPresenter<ChatsView>() {
    private var call: Disposable? = null

    fun loadChats(userId: Int) {
        call = dataRepository.getChats(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { chatResponse ->
                    if(chatResponse.isSuccessful)
                    {
                        viewState.showChats(chatResponse.body()!!)
                    } else {
                        Log.e("ChatsPresenter Error", chatResponse.message())
                    }
                },
                { throwable ->
                    // Обрабатываем ошибку, например, сетевую ошибку
                    Log.e("ApiError", "Error occurred: ${throwable.message}")
                }
            )
    }
}