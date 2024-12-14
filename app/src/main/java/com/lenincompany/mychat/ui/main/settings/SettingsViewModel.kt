package com.lenincompany.mychat.ui.main.settings

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

    private val _userPhoto = MutableLiveData<InputStream>()
    val userPhoto: LiveData<InputStream> get() = _userPhoto

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

    fun downloadUserPhoto() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = sharedPrefs.getUserId()
                val response = dataRepository.downloadUserPhoto(userId)
                if(response.isSuccessful)
                    if(response.body()!=null)
                        _userPhoto.postValue(response.body()!!.byteStream())
                    else
                        _errorMessage.postValue("No info")
                else {
                    _errorMessage.postValue("Failed to download user photo info: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error downloading user photo: ${e.message}")
            }
        }
    }

    fun uploadUserPhoto(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                val userId = sharedPrefs.getUserId()
                dataRepository.uploadUserPhoto(userId, filePart)
                downloadUserPhoto()
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
