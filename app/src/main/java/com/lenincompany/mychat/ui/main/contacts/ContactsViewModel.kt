package com.lenincompany.mychat.ui.main.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.Contact
import com.lenincompany.mychat.models.user.UserInfoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _usersInfo = MutableLiveData<List<UserInfoResponse>>()
    val usersInfo: LiveData<List<UserInfoResponse>?> get() = _usersInfo

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getUsersForPhone(users: List<Contact>)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.getUserForPhone(users.map { it.phone }.toList())
                if (response.isSuccessful) {
                    _usersInfo.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users info: ${e.message}")
            }
        }
    }
}