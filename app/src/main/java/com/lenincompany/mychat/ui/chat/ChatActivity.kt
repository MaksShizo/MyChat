package com.lenincompany.mychat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.databinding.ActivityChatsBinding
import com.lenincompany.mychat.models.Message
import com.lenincompany.mychat.ui.chats.ChatsRecyclerAdapter
import dagger.android.AndroidInjection
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.time.LocalDate
import javax.inject.Inject

class ChatActivity : MvpAppCompatActivity(), ChatView {
    @Inject
    lateinit var dataRepository: DataRepository

    @InjectPresenter
    lateinit var presenter: ChatPresenter
    private lateinit var binding: ActivityChatsBinding
    private lateinit var chatWebSocket: ChatWebSocket
    @ProvidePresenter
    fun providePresenter() = ChatPresenter(dataRepository)

    private lateinit var webSocket: ChatWebSocket
    private lateinit var rvAdapter: ChatRecyclerAdapter
    private var chatId = 0 // ID текущего чата
    private var userId = 0 // ID текущего чата
    private val messages = mutableListOf<Message>() // Список сообщений для адаптера
    private lateinit var recyclerView : RecyclerView
    companion object {

        private const val EXTRA_USER_ID = "extra_screen_type"
        private const val EXTRA_CHAT_ID = "extra_facility_id"
        /**
         * Creates [Intent] for starting [ChatActivity] with extra parameter
         */
        fun forIntent(
            packageContext: Context,
            chatId: Int,
            userId: Int,
        ): Intent {
            return Intent(packageContext, ChatActivity::class.java).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
                putExtra(EXTRA_USER_ID, userId)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rvAdapter = ChatRecyclerAdapter(messages) { message ->
            // Обработка кликов на сообщения, если нужно
        }
        recyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rvAdapter

        if(intent != null)
        {
            chatId = intent.getIntExtra(EXTRA_CHAT_ID, 0)
            userId = intent.getIntExtra(EXTRA_USER_ID, 0)
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
                    Log.e("WebSoket","WebSocket error: $error")

                }
            }
        )

        chatWebSocket.connect(chatId) // Передача chatId для фильтрации
        val sendButton = findViewById<ImageButton>(R.id.imageButton3)
        val messageEditText = findViewById<EditText>(R.id.editTextText)
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                val jsonMessage = Message(
                    ChatId = chatId,
                    UserId = userId,
                    Content = message,
                    DateCreate = LocalDate.now().toString(),
                    UserName = "maksim"
                )
                chatWebSocket.sendMessage(Json.encodeToString(jsonMessage)) // Используем encodeToString
                messageEditText.text.clear()
            }
        }

        presenter.getMessages(chatId)

    }

    private fun handleMessage(message: String) {
        // Преобразование JSON в объект и добавление в список
        val messageBody = parseMessage(message)
        messages.add(messageBody)
        rvAdapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun parseMessage(message: String): Message {
        return try {
            Json.decodeFromString<Message>(message)
        } catch (e: Exception) {
            Log.e("parsing","Error parsing message ${e.message}")
            Message(ChatId = chatId, UserId =  userId, Content =  "Parsing error", DateCreate = LocalDate.now().toString(), UserName = "Максим")
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

            else -> {false}
        }
    }

    override fun showMessage(messageResponse: List<Message>) {
        rvAdapter.setData(messageResponse)
    }
}