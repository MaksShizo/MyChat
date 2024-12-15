package com.lenincompany.mychat.ui.chat.edit

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.chat.ChatInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {
    private val _addUser = MutableLiveData<Boolean>()
    val addUser: LiveData<Boolean> get() = _addUser

    private val _chatInfo = MutableLiveData<ChatInfo>()
    val chatInfo: LiveData<ChatInfo> get() = _chatInfo

    private val _chatPhoto = MutableLiveData<String>()
    val chatPhoto: LiveData<String> get() = _chatPhoto

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

    fun uploadChatPhoto(chatId: Int, file: File, mimeType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                Log.d("test", mimeType)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val response = dataRepository.uploadChatPhoto(chatId, filePart)
                if(response.url!=null)
                    _chatPhoto.postValue(response.url!!)
            } catch (e: Exception) {
                _errorMessage.postValue("Error uploading photo: ${e.localizedMessage}")
            }
        }
    }
}