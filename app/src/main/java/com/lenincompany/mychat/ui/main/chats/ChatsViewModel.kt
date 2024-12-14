package com.lenincompany.mychat.ui.main.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.models.chat.ChatBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _chats = MutableLiveData<List<ChatBody>>()
    val chats: LiveData<List<ChatBody>> get() = _chats

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadChats(userId: Int) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.getChats(userId)
                if (response.isSuccessful) {
                    _chats.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chats info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chats info: ${e.message}")
            }
        }
    }
}