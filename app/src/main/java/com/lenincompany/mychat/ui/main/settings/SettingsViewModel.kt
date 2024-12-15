package com.lenincompany.mychat.ui.main.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.models.user.UserInfoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _userInfo = MutableLiveData<UserInfoResponse>()
    val userInfo: LiveData<UserInfoResponse> get() = _userInfo

    private val _userPhoto = MutableLiveData<String>()
    val userPhoto: LiveData<String> get() = _userPhoto

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = sharedPrefs.getUserId()
                val response = dataRepository.getUser(userId)
                if (response.isSuccessful) {
                    _userInfo.postValue(response.body())
                } else {
                    _errorMessage.postValue("Failed to fetch user info: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching user info: ${e.message}")
            }
        }
    }

    fun uploadUserPhoto(file: File, mimeType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                Log.d("test", mimeType)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val userId = sharedPrefs.getUserId()
                val response = dataRepository.uploadUserPhoto(userId, filePart)
                if(response.url!=null)
                    _userPhoto.postValue(response.url!!)
            //downloadUserPhoto()
            } catch (e: Exception) {
                _errorMessage.postValue("Error uploading photo: ${e.message}")
            }
        }
    }

    fun isUserInfoAvailable(): Boolean {
        return sharedPrefs.getEmail() != null && sharedPrefs.getUserName() != null
    }

    fun getUserName(): String? = sharedPrefs.getUserName()

    fun getUserEmail(): String? = sharedPrefs.getEmail()

    fun getUserId(): Int = sharedPrefs.getUserId()
}
