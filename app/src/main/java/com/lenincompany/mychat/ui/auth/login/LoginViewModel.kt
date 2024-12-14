package com.lenincompany.mychat.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.LoginRequest
import com.lenincompany.mychat.models.base.Token
import com.lenincompany.mychat.models.user.UserInfoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _login = MutableLiveData<Token>()
    val login: LiveData<Token> get() = _login

    private val _infoForUser = MutableLiveData<UserInfoResponse>()
    val infoForUser: LiveData<UserInfoResponse> get() = _infoForUser

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    _login.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
    }

    fun getInfoForUser(userId: Int) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.getUser(userId)
                if (response.isSuccessful) {
                    _infoForUser.postValue(response.body()!!)
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
    }
}