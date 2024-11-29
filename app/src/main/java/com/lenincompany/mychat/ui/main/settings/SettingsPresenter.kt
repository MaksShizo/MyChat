package com.lenincompany.mychat.ui.main.settings

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.lenincompany.mychat.data.DataRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.adapter.rxjava3.Result.response
import java.io.File
import javax.inject.Inject


@InjectViewState
class SettingsPresenter @Inject constructor(
    private val dataRepository: DataRepository
) : MvpPresenter<SettingsView>() {
    private var call: Disposable? = null

    fun getInfoForUser(userId: Int) {
        call = dataRepository.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userResponse ->
                    if (userResponse.isSuccessful) {
                        viewState.setInfoOnActivity(userResponse.body()!!)
                    } else {
                        Log.e("ChatsPresenter Error", userResponse.message())
                    }
                },
                { throwable ->
                    // Обрабатываем ошибку, например, сетевую ошибку
                    Log.e("ApiError", "Error occurred: ${throwable.message}")
                }
            )
    }

    fun uploadUserPhoto(context: Context, userId: Int, file: File?) {
        if (file != null) {
            val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

            // Отправка запроса
            val call = dataRepository.uploadPhoto(userId, filePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { messageResponse ->
                        if (messageResponse.message != null) {
                        } else {
                            Log.e("ChatsPresenter Error", messageResponse.message)
                        }
                    },
                    { throwable ->
                        // Обрабатываем ошибку, например, сетевую ошибку
                        Log.e("ApiError", "Error occurred: ${throwable.message}")
                    })
        }
    }

    fun downloadUserPhoto(userId: Int) {
        // Отправка запроса
        val call = dataRepository.downloadPhoto(userId)
            .subscribeOn(Schedulers.io())  // Отправляем запрос на фоновом потоке
            .observeOn(AndroidSchedulers.mainThread())  // Обрабатываем результат на главном потоке
            .subscribe(
                { responseBody ->
                    viewState.setPhoto(responseBody.body()!!.byteStream())
                },
                { throwable ->
                    Log.e("ApiError", "Error occurred: ${throwable.message}")
                }
            )
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    return it.getString(index) // Возвращает имя файла
                }
            }
        }
        return null
    }
}