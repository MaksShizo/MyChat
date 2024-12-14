package com.lenincompany.mychat.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _chat = MutableLiveData<List<Message>>()
    val chat: LiveData<List<Message>> get() = _chat

    private val _users = MutableLiveData<List<ChatUsers>>()
    val users: LiveData<List<ChatUsers>> get() = _users

    private val _usersPhoto = MutableLiveData<Pair<ResponseBody, Int>>()
    val usersPhoto: LiveData<Pair<ResponseBody, Int>> get() = _usersPhoto

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    fun getMessages(chatId: Int)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.getMessagesInChat(chatId)
                if (response.isSuccessful) {
                    _chat.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
        //showMessage
    }

    fun getUsers(chatId: Int)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.getUsersInChat(chatId)
                if (response.isSuccessful) {
                    _users.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chat users info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users info: ${e.message}")
            }
        }
        //viewState.setUser(messageResponse.body()!!)
    }

    fun downloadUserPhoto(userId: Int) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.downloadUserPhoto(userId)
                if (response.isSuccessful) {
                    _usersPhoto.postValue(Pair(response.body()!!, userId))
                } else {
                    _errorMessage.postValue("Failed to load chat users photo info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users photo info: ${e.message}")
            }
        }
        //viewState.savePhoto(responseBody.body()!!.byteStream(), userId)
    }
}