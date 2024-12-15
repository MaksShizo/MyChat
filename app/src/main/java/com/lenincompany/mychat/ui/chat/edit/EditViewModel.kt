package com.lenincompany.mychat.ui.chat.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.chat.ChatInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

class EditViewModel@Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {
    private val _addUser = MutableLiveData<Boolean>()
    val addUser: LiveData<Boolean> get() = _addUser

    private val _chatInfo = MutableLiveData<ChatInfo>()
    val chatInfo: LiveData<ChatInfo> get() = _chatInfo

    private val _chatPhoto = MutableLiveData<ResponseBody>()
    val chatPhoto: LiveData<ResponseBody> get() = _chatPhoto

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addUser(chatId: Int, userId: Int)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.addUserInChat(chatId, userId)
                if (response.isSuccessful) {
                    _addUser.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
    }

    fun getGroupChatInfo(chatId: Int)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.getGroupChatInfo(chatId)
                if (response.isSuccessful) {
                    _chatInfo.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to load chat messages info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat messages info: ${e.message}")
            }
        }
    }

    fun getChatPhoto(chatId: Int) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.downloadChatPhoto(chatId)
                if (response.isSuccessful) {
                    _chatPhoto.postValue(response.body()!!)
                } else {
                    _errorMessage.postValue("Failed to load chat users photo info: ${response.message()}")
                }
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users photo info: ${e.message}")
            }
        }
    }

    fun uploadChatPhoto(chatId: Int, file: File) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                dataRepository.uploadChatPhoto(chatId, filePart)
                getChatPhoto(chatId)
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users photo info: ${e.message}")
            }
        }
    }
}