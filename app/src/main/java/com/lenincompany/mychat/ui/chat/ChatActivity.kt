package com.lenincompany.mychat.ui.chat

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityChatBinding
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.chat.UsersPhoto
import dagger.android.AndroidInjection
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.io.InputStream
import java.time.LocalDate
import java.util.Dictionary
import javax.inject.Inject


class ChatActivity : MvpAppCompatActivity(), ChatView {
    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @InjectPresenter
    lateinit var presenter: ChatPresenter
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatWebSocket: ChatWebSocket

    @ProvidePresenter
    fun providePresenter() = ChatPresenter(dataRepository)
    private lateinit var rvAdapter: ChatRecyclerAdapter
    private var chatId = 0 // ID текущего чата
    private var userId = 0 // ID пользователя
    private var nameChat = "" // Название чата
    private var usersPhoto = mutableListOf<UsersPhoto>() // Список сообщений для адаптера
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
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
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
            showChatInfoDialog()
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
            val message = binding.editTextText.text.toString()
            if (message.isNotEmpty()) {
                val jsonMessage = Message(
                    ChatId = chatId,
                    UserId = userId,
                    Content = message,
                    DateCreate = LocalDate.now().toString(),
                )
                chatWebSocket.sendMessage(Json.encodeToString(jsonMessage)) // Используем encodeToString
                binding.editTextText.text.clear()
            }
        }
        presenter.getUsers(chatId)
        presenter.getMessages(chatId)
    }

    private fun handleMessage(message: String) {
        // Преобразование JSON в объект и добавление в список
        val messageBody = parseMessage(message)
        messages.add(messageBody)
        rvAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.smoothScrollToPosition(messages.size - 1)
    }

    private fun showChatInfoDialog() {
        // Здесь вы можете показать информацию о чате
        AlertDialog.Builder(this)
            .setTitle("Информация о чате")
            .setMessage("Здесь отображается информация о текущем чате.")
            .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
            .show()
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
                DateCreate = LocalDate.now().toString()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatWebSocket.disconnect()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }

            else -> {
                false
            }
        }
    }

    override fun showMessage(messageResponse: List<Message>) {
        rvAdapter.setData(messageResponse, users)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    override fun setUser(usersResponse: List<ChatUsers>) {
        users = usersResponse
        usersResponse.forEach {
            if (it.Photo != null) {
                presenter.downloadUserPhoto(it.UserId)
            }
        }
    }

    override fun savePhoto(inputStream: InputStream, userId: Int) {
        try {
            val byteArray = inputStream.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            inputStream.close()
            rvAdapter.addUsersPhoto(UsersPhoto(userId, bitmap))
        } catch (e: Exception) {
            Log.e("ApiError", "Error reading photo data: ${e.message}")
        }
    }
}