package com.lenincompany.mychat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityChatBinding
import com.lenincompany.mychat.databinding.ItemOtherMessageBinding
import com.lenincompany.mychat.databinding.ItemUserMessageBinding
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.ui.chat.edit.EditActivity
import com.lenincompany.mychat.ui.chat.fullscreen.FullscreenImageFragment
import com.lenincompany.mychat.ui.chat.fullscreen.FullscreenVideoFragment
import com.lenincompany.mychat.utils.VideoSaver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private val chatViewModel: ChatViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val fileChooserRequestCode = 1001
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatWebSocket: ChatWebSocket
    private lateinit var rvAdapter: ChatRecyclerAdapter
    private var files : Pair<File, String>? = null
    private var chatId = 0 // ID текущего чата
    private var userId = 0 // ID пользователя
    private var nameChat = "" // Название чата
    private var users = listOf<ChatUsers>() // Список сообщений для адаптера
    private val messages = mutableListOf<Message>() // Список сообщений для адаптера
    private lateinit var recyclerView: RecyclerView

    companion object {

        private const val EXTRA_USER_ID = "extra_user_id"
        private const val EXTRA_CHAT_ID = "extra_chat_id"
        private const val EXTRA_NAME_CHAT = "extra_name_chat"
        /**
         * Creates [Intent] for starting [ChatActivity] with extra parameter
         */
        fun forIntent(
            packageContext: Context,
            nameChat: String,
            chatId: Int,
            userId: Int,
        ): Intent {
            return Intent(packageContext, ChatActivity::class.java).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
                putExtra(EXTRA_USER_ID, userId)
                putExtra(EXTRA_NAME_CHAT, nameChat)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setupObservers()
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rvAdapter =
            ChatRecyclerAdapter(messages, { message -> }, sharedPrefs.getUserId(), mutableListOf(), mutableListOf())
        recyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rvAdapter
        recyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) recyclerView.smoothScrollToPosition(messages.size - 1)
        }
        if (intent != null) {
            chatId = intent.getIntExtra(EXTRA_CHAT_ID, 0)
            userId = intent.getIntExtra(EXTRA_USER_ID, 0)
            nameChat = intent.getStringExtra(EXTRA_NAME_CHAT)!!
        }

        binding.chatCustomToolbar.chatTitle.text = nameChat
        // Добавьте обработчик клика
        binding.chatCustomToolbar.chatTitle.setOnClickListener {
            showChatInfo(chatId)
        }
        chatWebSocket = ChatWebSocket(
            serverUrl = "ws://10.0.2.2:5093/ws",
            onMessageReceived = { message ->
                runOnUiThread {
                    handleMessage(message)
                }
            },
            onOpen = {
                runOnUiThread {
                    Toast.makeText(this, "Connected to WebSocket", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "WebSocket error: $error", Toast.LENGTH_SHORT).show()
                    Log.e("WebSoket", "WebSocket error: $error")

                }
            }
        )

        chatWebSocket.connect(chatId) // Передача chatId для фильтрации
        binding.imageButton3.setOnClickListener {
            if(files!=null)
            {
                chatViewModel.uploadChatFile(chatId, files!!.first, files!!.second)
                val message = binding.editTextText.text.toString()
                if(message.isNotBlank())
                    sendMessage(message, Message.TEXT)
            }else
            {
                val message = binding.editTextText.text.toString()
                if(message.isNotBlank())
                    sendMessage(message, Message.TEXT)
            }
        }
        binding.addFiles.setOnClickListener{
            openFileChooser()
        }
        chatViewModel.getUsers(chatId)
    }

    private fun setupObservers() {
        chatViewModel.chat.observe(this) { chat ->
            showMessage(chat)
        }

        chatViewModel.chatFile.observe(this) { chatFile ->
            sendMessage(chatFile.url!!,chatFile.type!!)
        }

        chatViewModel.users.observe(this) { users ->
            setUser(users)
            chatViewModel.getMessages(chatId)
        }

        chatViewModel.usersPhoto.observe(this) { usersPhoto ->
            rvAdapter.addUsersPhoto(usersPhoto)
        }


        chatViewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }

    fun sendMessage(message: String, type: Short){
        if (message.isNotEmpty()) {
            val jsonMessage = Message(
                ChatId = chatId,
                UserId = userId,
                Content = message,
                DateCreate = LocalDate.now().toString(),
                Type = type
            )
            chatWebSocket.sendMessage(Json.encodeToString(jsonMessage))
            binding.editTextText.text.clear()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "Select File"), fileChooserRequestCode)
    }

    private fun handleMessage(message: String) {
        // Преобразование JSON в объект и добавление в список
        val messageBody = parseMessage(message)
        messages.add(messageBody)
        rvAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.smoothScrollToPosition(messages.size - 1)
    }

    private fun showChatInfo(chatId: Int) {
        startActivity(
            EditActivity.forIntent(
                packageContext = this,
                chatId = chatId
            )
        )
    }

    private fun parseMessage(message: String): Message {
        return try {
            Json.decodeFromString<Message>(message)
        } catch (e: Exception) {
            Log.e("parsing", "Error parsing message ${e.message}")
            Message(
                ChatId = chatId,
                UserId = userId,
                Content = "Parsing error",
                DateCreate = LocalDate.now().toString(),
                Type = Message.ERROR
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatWebSocket.disconnect()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }

            else -> {
                false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == fileChooserRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val selectedFileUri: Uri? = data.data
            if (selectedFileUri != null) {
                val mimeType = contentResolver.getType(selectedFileUri)
                val file = getDriveFilePath(selectedFileUri)
                if (file != null) {
                    if (mimeType != null) {
                        files  = Pair(file, mimeType)
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

    fun showMessage(messageResponse: List<Message>) {
        rvAdapter.setData(messageResponse, users)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    fun setUser(usersResponse: List<ChatUsers>) {
        users = usersResponse
        chatViewModel.getUserPhotos(usersResponse)
    }

    // В вашем Activity или Fragment
    fun openFullscreenImage(message: Message) {
        val fragment = FullscreenImageFragment.newInstance(message.Content)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun openFullscreenVideo(message: Message, videoUri: Uri) {
        val fragment = FullscreenVideoFragment.newInstance(videoUri)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun playVideo(binding: ItemUserMessageBinding, videoUri: Uri) {
        binding.playBtn.isGone = true
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()
    }

    fun downloadVideoAndPlay(binding: ItemUserMessageBinding, message: Message) {
        binding.downloadBtn.isGone = true
        binding.progressBar.isVisible = true
        chatViewModel.downloadVideo(binding,message,baseContext)
    }

    fun playVideo(binding: ItemOtherMessageBinding, videoUri: Uri) {
        binding.playBtn.isGone = true
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()
    }

    fun downloadVideoAndPlay(binding: ItemOtherMessageBinding, message: Message) {
        binding.downloadBtn.isGone = true
        binding.progressBar.isVisible = true
        chatViewModel.downloadVideo(binding,message,baseContext)
    }

}