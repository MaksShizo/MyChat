package com.lenincompany.mychat.ui.main.chats

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenincompany.mychat.data.DataRepository
import com.lenincompany.mychat.data.SharedPrefs
import com.lenincompany.mychat.databinding.FragmentChatsBinding
import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.ui.chat.ChatActivity
import dagger.android.support.AndroidSupportInjection
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class ChatsFragment : MvpAppCompatFragment(), ChatsView {
    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @InjectPresenter
    lateinit var presenter: ChatsPresenter
    private lateinit var binding: FragmentChatsBinding
    private lateinit var rvAdapter: ChatsRecyclerAdapter

    @ProvidePresenter
    fun providePresenter() = ChatsPresenter(dataRepository)

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

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
        rvAdapter = ChatsRecyclerAdapter(mutableListOf()) { chat ->
            onChatClicked(chat)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = rvAdapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            presenter.loadChats(sharedPrefs.getUserId())
        }
        presenter.loadChats(sharedPrefs.getUserId())
    }

    private fun onChatClicked(chat: ChatBody) {
        startActivity(
            ChatActivity.forIntent(
                packageContext = requireContext(),
                userId = sharedPrefs.getUserId(),
                chatId = chat.chatId
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showChats(chatResponse: List<ChatBody>) {
        rvAdapter.setData(chatResponse)
        binding.swipeRefreshLayout.isRefreshing = false
        Toast.makeText(requireContext(), "Chats loaded: ${chatResponse.size}", Toast.LENGTH_SHORT).show()
    }
}