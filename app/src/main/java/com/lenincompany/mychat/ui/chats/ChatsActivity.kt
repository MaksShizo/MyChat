package com.lenincompany.mychat.ui.chats

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.models.ChatBody
import dagger.android.AndroidInjection
import moxy.MvpAppCompatActivity
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class ChatsActivity : MvpAppCompatActivity(), ChatsView {

    @Inject
    lateinit var dataRepository: DataRepository

    @InjectPresenter
    lateinit var presenter: ChatsPresenter

    @ProvidePresenter
    fun providePresenter() = ChatsPresenter(dataRepository)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        // Инжектируем зависимости через Dagger
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats)
        setSupportActionBar(findViewById(R.id.toolbar))
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            presenter.loadChats(1)
        }


    }

    override fun showChats(chats: List<ChatBody>) {
        // Реализуйте отображение чатов
        Toast.makeText(this, "Chats loaded: ${chats.size}", Toast.LENGTH_SHORT).show()
    }
}