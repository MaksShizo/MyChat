package com.lenincompany.mychat.ui.createChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lenincompany.mychat.data.DataRepository
import javax.inject.Inject

class CreateChatViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage
}