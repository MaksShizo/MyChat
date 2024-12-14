package com.lenincompany.mychat.ui.chat

import android.util.Log
import com.lenincompany.mychat.data.DataRepository
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
        call = dataRepository.getMessagesInChat(chatId)
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

    fun getUsers(chatId: Int)
    {
        call = dataRepository.getUsersInChat(chatId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messageResponse ->
                    if(messageResponse.isSuccessful)
                    {
                        viewState.setUser(messageResponse.body()!!)
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

    fun downloadUserPhoto(userId: Int) {
        // Отправка запроса
        val call = dataRepository.downloadUserPhoto(userId)
            .subscribeOn(Schedulers.io())  // Отправляем запрос на фоновом потоке
            .observeOn(AndroidSchedulers.mainThread())  // Обрабатываем результат на главном потоке
            .subscribe(
                { responseBody ->
                    viewState.savePhoto(responseBody.body()!!.byteStream(), userId)
                },
                { throwable ->
                    Log.e("ApiError", "Error occurred: ${throwable.message}")
                }
            )
    }

}