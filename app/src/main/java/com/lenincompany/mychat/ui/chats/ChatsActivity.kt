package com.lenincompany.mychat.ui.chats

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.data.TokenManager
import com.lenincompany.mychat.databinding.ActivityChatBinding
import com.lenincompany.mychat.databinding.ActivityChatsBinding
import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.ui.chat.ChatActivity
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class ChatsActivity : MvpAppCompatActivity(), ChatsView {

    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @InjectPresenter
    lateinit var presenter: ChatsPresenter
    private lateinit var binding: ActivityChatsBinding
    private lateinit var rvAdapter: ChatsRecyclerAdapter

    @ProvidePresenter
    fun providePresenter() = ChatsPresenter(dataRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        // Инициализируем адаптер с пустым списком
        rvAdapter = ChatsRecyclerAdapter(mutableListOf(), ) { chat ->
            onChatClicked(chat)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter.loadChats(sharedPrefs.getUserId())
        }
        // Настройка RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rvAdapter
        presenter.loadChats(sharedPrefs.getUserId())

    }

    private fun onChatClicked(chat: ChatBody) {
        startActivity(
            ChatActivity.forIntent(
                packageContext = this,
                userId = sharedPrefs.getUserId(),
                chatId = chat.chatId
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showChats(chatResponse: List<ChatBody>) {
        rvAdapter.setData(chatResponse)
        binding.swipeRefreshLayout.isRefreshing = false
        Toast.makeText(this, "Chats loaded: ${chatResponse.size}", Toast.LENGTH_SHORT).show()
    }
}