package com.lenincompany.mychat.network

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.TokenManager
import com.lenincompany.mychat.models.Token
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class TokenRefresher(
    private val dataRepository: DataRepository,
    private val tokenManager: TokenManager
) {
    private val handler = Handler(Looper.getMainLooper())
    private var refreshIntervalMillis: Long = 15 * 60 * 1000 // 15 минут

    private val refreshRunnable = object : Runnable {
        override fun run() {
            refreshToken()
            // Планируем следующий вызов через 15 минут
            handler.postDelayed(this, refreshIntervalMillis)
        }
    }

    // Запускаем процесс обновления
    fun start() {
        handler.post(refreshRunnable)
    }

    // Останавливаем процесс обновления
    fun stop() {
        handler.removeCallbacks(refreshRunnable)
    }

    // Обновляем токен
    @SuppressLint("CheckResult")
    private fun refreshToken() {
        val userId = tokenManager.getUserId()
        val refreshToken = tokenManager.getRefreshToken()
        val accessToken = tokenManager.getAccessToken()

        if (userId == -1 || refreshToken.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
            Log.e("TokenRefresher", "Missing token or userId")
            return
        }

        dataRepository.refresh(Token(userId, refreshToken, accessToken))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.isSuccessful && response.body() != null) {
                        val newAccessToken = response.body()!!.AccessToken
                        val newRefreshToken = response.body()!!.RefreshToken

                        // Сохраняем новые токены
                        tokenManager.saveTokens(userId, newAccessToken, newRefreshToken)
                        Log.d("TokenRefresher", "Tokens updated successfully")
                    } else {
                        Log.e("TokenRefresher", "Failed to refresh token: ${response.message()}")
                    }
                },
                { throwable ->
                    Log.e("TokenRefresher", "Error occurred: ${throwable.message}")
                }
            )
    }
}