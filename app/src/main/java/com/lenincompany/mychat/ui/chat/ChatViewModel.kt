package com.lenincompany.mychat.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.base.MessageServer
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.chat.UsersPhoto
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {

    private val _chat = MutableLiveData<List<Message>>()
    val chat: LiveData<List<Message>> get() = _chat

    private val _chatFile = MutableLiveData<MessageServer>()
    val chatFile: LiveData<MessageServer> get() = _chatFile

    private val _users = MutableLiveData<List<ChatUsers>>()
    val users: LiveData<List<ChatUsers>> get() = _users

    private val _usersPhoto = MutableLiveData<List<UsersPhoto>>()
    val usersPhoto: LiveData<List<UsersPhoto>> get() = _usersPhoto

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
    }

    fun getUserPhotos(users: List<ChatUsers>)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val photos = mutableListOf<UsersPhoto>()
                users.forEach {
                    if(it.Photo!=null)
                        photos.add(UsersPhoto(it.UserId, Picasso.get().load(it.Photo).get()))
                }
                _usersPhoto.postValue(photos)
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users info: ${e.message}")
            }
        }
    }

    fun uploadChatFile(chatId: Int, file: File, mimeType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                Log.d("test", mimeType)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val response = dataRepository.uploadChatFile(chatId, filePart)
                if(response.url!=null)
                    _chatFile.postValue(response)
            } catch (e: Exception) {
                _errorMessage.postValue("Error uploading photo: ${e.localizedMessage}")
            }
        }
    }
}