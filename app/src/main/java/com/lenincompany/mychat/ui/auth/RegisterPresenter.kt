package com.lenincompany.mychat.ui.auth

import android.util.Log
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.user.UserResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

class RegisterPresenter(
    private val dataRepository: DataRepository
) : MvpPresenter<RegisterView>() {
    private var call: Disposable? = null

    fun register(name: String,email: String, password: String) {
        call = dataRepository.register(
            UserResponse(
            Email = email,
            Name = name,
            Password = password
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messageResponse ->
                    if (messageResponse.isSuccessful) {
                        viewState.confirmRegister()
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

    fun resetPassword(email: String) {
        call = dataRepository.resetPassword(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messageResponse ->
                    if (messageResponse.isSuccessful) {

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