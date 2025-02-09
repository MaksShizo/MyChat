package com.lenincompany.mychat.ui.createChat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.R
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.ActivityChatBinding
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.ui.chat.ChatActivity
import com.lenincompany.mychat.ui.chat.ChatActivity.Companion
import com.lenincompany.mychat.ui.chat.ChatRecyclerAdapter
import com.lenincompany.mychat.ui.chat.ChatWebSocket
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateChatActivity : AppCompatActivity() {
    private val createChatViewModel: CreateChatViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private lateinit var binding: ActivityChatBinding
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
    }

    private fun setupObservers() {
        createChatViewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }
}