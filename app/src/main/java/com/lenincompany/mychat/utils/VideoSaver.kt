package com.lenincompany.mychat.utils

import android.content.Context
import android.net.Uri
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class VideoSaver {
    fun isFileExists(directory: File, fileName: String): Boolean {
        if (!directory.exists() || !directory.isDirectory) return false
        val files = directory.listFiles()
        return files?.any { it.name == fileName } == true
    }

    fun isFileExistsFromUrl(context: Context, fileName: String): Boolean {
        val directory = File(context.getExternalFilesDir(null), "videos")
        if (!directory.exists() || !directory.isDirectory) return false
        val files = directory.listFiles()
        val name = getFileNameFromUrl(fileName)
        return files?.any { it.name == name } == true
    }

    fun getFileUriFromUrl(context: Context, url: String): Uri? {
        val directory = File(context.getExternalFilesDir(null), "videos")
        if (!directory.exists() || !directory.isDirectory) return null
        val fileName = getFileNameFromUrl(url)
        val file = directory.listFiles()?.find { it.name == fileName }
        return file?.let { Uri.fromFile(it) }
    }

    fun downloadVideo(url: String, context: Context, onProgress: (progress: Int) -> Unit): Boolean {
        val fileName = getFileNameFromUrl(url)
        val directory = File(context.getExternalFilesDir(null), "videos")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val destinationFile = File(directory, fileName)
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return false
            val inputStream: InputStream = response.body!!.byteStream()
            val outputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            var totalBytesRead = 0L
            val fileSize = response.body!!.contentLength()
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                onProgress((totalBytesRead * 100 / fileSize).toInt())
            }
            outputStream.close()
            inputStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getFileNameFromUrl(url: String): String {
        return url.substringAfterLast("/")
    }

}