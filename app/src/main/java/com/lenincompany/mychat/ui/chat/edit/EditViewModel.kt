package com.lenincompany.mychat.ui.chat.edit

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.Contact
import com.lenincompany.mychat.models.chat.ChatInfo
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.GroupChatUser
import com.lenincompany.mychat.models.chat.UsersPhoto
import com.lenincompany.mychat.models.user.UserInfoResponse
import com.squareup.picasso.Picasso
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

    private val _usersInfo = MutableLiveData<List<UserInfoResponse>>()
    val usersInfo: LiveData<List<UserInfoResponse>?> get() = _usersInfo

    private val _chatInfo = MutableLiveData<ChatInfo>()
    val chatInfo: LiveData<ChatInfo> get() = _chatInfo

    private val _chatPhoto = MutableLiveData<String>()
    val chatPhoto: LiveData<String> get() = _chatPhoto

    private val _usersPhoto = MutableLiveData<List<UsersPhoto>>()
    val usersPhoto: LiveData<List<UsersPhoto>> get() = _usersPhoto


    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addUser(chatId: Int, userId: List<Int>)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = dataRepository.addUsersInChat(chatId, userId)
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

    fun getUserPhotos(users: List<GroupChatUser>)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val photos = mutableListOf<UsersPhoto>()
                users.forEach {
                    if(it.user.Photo!=null)
                        photos.add(UsersPhoto(it.userId, Picasso.get().load(it.user.Photo).get()))
                }
                _usersPhoto.postValue(photos)
            }catch (e: Exception) {
                _errorMessage.postValue("Error load chat users info: ${e.message}")
            }
        }
    }

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