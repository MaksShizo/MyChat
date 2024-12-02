package com.lenincompany.mychat.data

import android.content.Context
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.user.UserInfoResponse
import javax.inject.Inject

class SharedPrefs @Inject constructor(val context: Context)
{
    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_CREATE_DATE = "user_create_date"
    }

    // Сохраняем данные токена
    fun saveTokens(userId: Int, accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }

    // Сохраняем данные токена
    fun saveTokens(token : Token) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, token.UserId)
            putString(KEY_ACCESS_TOKEN, token.AccessToken)
            putString(KEY_REFRESH_TOKEN, token.RefreshToken)
            apply()
        }
    }

    fun saveUser(userInfoResponse: UserInfoResponse){
        sharedPreferences.edit().apply{
            putString(KEY_EMAIL, userInfoResponse.Email)
            putString(KEY_USER_NAME, userInfoResponse.Name)
            putString(KEY_CREATE_DATE, userInfoResponse.CreateDate)
            apply()
        }
    }

    fun getEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)

    fun getUserName(): String? = sharedPreferences.getString(KEY_USER_NAME, null)

    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): Int = sharedPreferences.getInt(KEY_USER_ID, -1)

    // Очищаем данные токена
    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }


}