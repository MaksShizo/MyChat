package com.lenincompany.mychat.ui.main.chats

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.FragmentChatsBinding
import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.ui.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : Fragment() {
    private val chatsViewModel: ChatsViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private lateinit var binding: FragmentChatsBinding
    private lateinit var rvAdapter: ChatsRecyclerAdapter

    companion object {
        internal const val ARG_TYPE = "arg_type"
        /**
         * Returns created [WorksFragment] with given filter fragmentType
         */
        fun newInstance(type: String): ChatsFragment {
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            return ChatsFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        rvAdapter = ChatsRecyclerAdapter(mutableListOf()) { chat ->
            onChatClicked(chat)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = rvAdapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            chatsViewModel.loadChats(sharedPrefs.getUserId())
        }
        chatsViewModel.loadChats(sharedPrefs.getUserId())
    }

    private fun setupObservers() {
        chatsViewModel.chats.observe(viewLifecycleOwner) { chats ->
            showChats(chats)
        }

        chatsViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("ChatsFragment", "Error: $errorMessage")
        }
    }

    private fun onChatClicked(chat: ChatBody) {
        startActivity(
            ChatActivity.forIntent(
                packageContext = requireContext(),
                nameChat = chat.name,
                userId = sharedPrefs.getUserId(),
                chatId = chat.chatId
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showChats(chatResponse: List<ChatBody>) {
        rvAdapter.setData(chatResponse)
        binding.swipeRefreshLayout.isRefreshing = false
        Toast.makeText(requireContext(), "Chats loaded: ${chatResponse.size}", Toast.LENGTH_SHORT).show()
    }
}