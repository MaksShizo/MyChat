package com.lenincompany.mychat.ui.chat.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.databinding.ActivityChatEditBinding
import com.lenincompany.mychat.models.chat.ChatInfo
import com.lenincompany.mychat.ui.chat.ChatActivity
import com.lenincompany.mychat.ui.main.settings.SettingsFragment.Companion.PICK_IMAGE_REQUEST_CODE
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChatEditBinding
    private val editViewModel: EditViewModel by viewModels()
    private lateinit var rvAdapter: EditChatUsersRecyclerAdapter
    private var chatId = 0
    companion object {
        private const val EXTRA_CHAT_ID = "extra_chat_id"
        /**
         * Creates [Intent] for starting [ChatActivity] with extra parameter
         */
        fun forIntent(
            packageContext: Context,
            chatId: Int,
        ): Intent {
            return Intent(packageContext, EditActivity::class.java).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatEditBinding.inflate(layoutInflater)
        rvAdapter = EditChatUsersRecyclerAdapter(mutableListOf(), { message -> }, mutableListOf())
        binding.usersRv.layoutManager = LinearLayoutManager(this)
        binding.usersRv.adapter = rvAdapter
        setupObservers()
        setContentView(binding.root)

        if (intent != null) {
            chatId = intent.getIntExtra(EXTRA_CHAT_ID, 0)
        }
        editViewModel.getGroupChatInfo(chatId)
        binding.addImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE)
        }
    }

    private fun setupObservers() {
        editViewModel.addUser.observe(this) { chat ->

        }

        editViewModel.chatInfo.observe(this) { chatInfo ->
            setChatsInfo(chatInfo)
        }

        editViewModel.chatPhoto.observe(this) { chatPhoto ->
            Picasso.get().load(chatPhoto).into(binding.imageChat)
        }

        editViewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                val mimeType = contentResolver.getType(selectedImageUri)
                val file = getDriveFilePath(selectedImageUri)
                if (file != null) {
                    // Если MIME-тип известен, передаем его при загрузке
                    if (mimeType != null) {
                        editViewModel.uploadChatPhoto(chatId, file, mimeType)
                    } else {
                        // Если MIME-тип не определился, можно попробовать по умолчанию
                        editViewModel.uploadChatPhoto(chatId, file, "image/*")
                    }
                }
            }
        }
    }


    private fun getDriveFilePath(uri: Uri): File? {
        val file = File(this.cacheDir, uri.lastPathSegment!!)
        try {
            val instream: InputStream = this.contentResolver.openInputStream(uri)!!
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int
            while (instream.read(buffer).also { size = it } != -1) {
                output.write(buffer, 0, size)
            }
            instream.close()
            output.close()
            return file
        } catch (e: IOException) {
            Log.d("SettingsFragment", "Error creating file: ${e}")
            return null
        }
    }

    private fun setChatsInfo(chatInfo: ChatInfo) {
        binding.nameTv.text = chatInfo.name
        if(chatInfo.photo!=null)
            Picasso.get().load(chatInfo.photo).into(binding.imageChat)
        rvAdapter.setData(chatInfo.groupChatUsers)
    }

}