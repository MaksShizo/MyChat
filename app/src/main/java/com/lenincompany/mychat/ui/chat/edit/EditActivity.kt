package com.lenincompany.mychat.ui.chat.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lenincompany.mychat.databinding.ActivityChatEditBinding
import com.lenincompany.mychat.ui.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditActivity : AppCompatActivity(){

    private lateinit var binding: ActivityChatEditBinding
    private val editViewModel: EditViewModel by viewModels()


    companion object {
        private const val EXTRA_CHAT_ID = "extra_chat_id"
        /**
         * Creates [Intent] for starting [ChatActivity] with extra parameter
         */
        fun forIntent(
            packageContext: Context,
            chatId: Int,
        ): Intent {
            return Intent(packageContext, ChatActivity::class.java).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatEditBinding.inflate(layoutInflater)
        setupObservers()
        setContentView(binding.root)
        var chatId = 0
        if (intent != null) {
            chatId = intent.getIntExtra(EditActivity.EXTRA_CHAT_ID, 0)
        }

        editViewModel.getGroupChatInfo(chatId)
        editViewModel.getChatPhoto(chatId)
    }

    private fun setupObservers() {
        editViewModel.addUser.observe(this) { chat ->

        }

        editViewModel.chatInfo.observe(this) { chat ->

        }

        editViewModel.addUser.observe(this) { chat ->

        }

        editViewModel.errorMessage.observe(this) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }

}