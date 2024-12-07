package com.lenincompany.mychat.ui.auth

import android.util.Log
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.LoginRequest
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

class LoginPresenter(
    private val dataRepository: DataRepository
) : MvpPresenter<LoginView>() {
    private var call: Disposable? = null

    fun login(email: String, password: String) {
        call = dataRepository.login(LoginRequest(email, password))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messageResponse ->
                    if (messageResponse.isSuccessful) {
                        viewState.setupTokenRefresher(messageResponse.body()!!)
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

    fun getInfoForUser(userId: Int) {
        call = dataRepository.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userResponse ->
                    if (userResponse.isSuccessful) {
                        viewState.saveUserInfo(userResponse.body()!!)
                    } else {
                        Log.e("ChatsPresenter Error", userResponse.message())
                    }
                },
                { throwable ->
                    // Обрабатываем ошибку, например, сетевую ошибку
                    Log.e("ApiError", "Error occurred: ${throwable.message}")
                }
            )
    }
}