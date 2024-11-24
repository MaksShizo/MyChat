package com.lenincompany.mychat.ui.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.databinding.ActivityChatsBinding
import com.lenincompany.mychat.models.ChatBody
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.time.LocalDate
import javax.inject.Inject

class ChatsActivity : MvpAppCompatActivity(), ChatsView {

    @Inject
    lateinit var dataRepository: DataRepository

    @InjectPresenter
    lateinit var presenter: ChatsPresenter
    private lateinit var binding: ActivityChatsBinding
    private lateinit var rvAdapter: ChatRecyclerAdapter

    @ProvidePresenter
    fun providePresenter() = ChatsPresenter(dataRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        // Инжектируем зависимости через Dagger
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats)
        setSupportActionBar(findViewById(R.id.toolbar))
        // Инициализируем адаптер с пустым списком
        rvAdapter = ChatRecyclerAdapter(mutableListOf()) { chat ->
            onChatClicked(chat)
        }
        // Настройка RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = rvAdapter
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            presenter.loadChats(1)
        }


    }

    private fun onChatClicked(chat: ChatBody) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showChats(chatResponse: List<ChatBody>) {
        rvAdapter.setData(chatResponse)
        Toast.makeText(this, "Chats loaded: ${chatResponse.size}", Toast.LENGTH_SHORT).show()
    }
}