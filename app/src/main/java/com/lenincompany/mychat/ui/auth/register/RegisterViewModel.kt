package com.lenincompany.mychat.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.user.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _register = MutableLiveData<Unit>()
    val register: LiveData<Unit> get() = _register

    private val _resetPassword = MutableLiveData<Unit>()
    val resetPassword: LiveData<Unit> get() = _resetPassword

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.register(
                    UserResponse(
                    Email = email,
                    Name = name,
                    Password = password
                ))
                if (response.isSuccessful) {
                    _register.postValue(Unit)
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.resetPassword(email)
                if (response.isSuccessful) {
                    _resetPassword.postValue(Unit)
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
    }
}