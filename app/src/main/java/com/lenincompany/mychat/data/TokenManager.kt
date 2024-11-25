package com.lenincompany.mychat.data

import android.content.Context

class TokenManager(context: Context) {

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
    }

    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Сохраняем данные токена
    fun saveTokens(userId: Int, accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    // Получаем данные токена
    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): Int = sharedPreferences.getInt(KEY_USER_ID, -1)

    // Очищаем данные токена
    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
}