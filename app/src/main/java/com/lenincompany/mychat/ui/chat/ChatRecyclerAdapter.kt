package com.lenincompany.mychat.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemOtherMessageBinding
import com.lenincompany.mychat.databinding.ItemUserMessageBinding
import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.models.Message

class ChatRecyclerAdapter(
    private var data: MutableList<Message>,
    private val onChatClick: (Message) -> Unit,
    private val userId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_USER_MESSAGE = 0
        const val TYPE_OTHER_MESSAGE = 1
    }

    // ViewHolder для сообщений пользователя
    class UserMessageViewHolder(private val binding: ItemUserMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            Log.d("UserMessageViewHolder", "Binding chat: ${message.Content}")
            val context = itemView.context
            binding.messageTv.text = message.Content
        }
    }

    // ViewHolder для сообщений собеседника
    class OtherMessageViewHolder(private val binding: ItemOtherMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            Log.d("OtherMessageViewHolder", "Binding chat: ${message.Content}")
            val context = itemView.context
            binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
            binding.userName.text = message.UserName
            binding.messageTv.text = message.Content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER_MESSAGE -> {
                val binding = ItemUserMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserMessageViewHolder(binding)
            }
            TYPE_OTHER_MESSAGE -> {
                val binding = ItemOtherMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OtherMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChats: List<Message>) {
        Log.d("ChatRecyclerAdapter", "Setting new data, size: ${newChats.size}")
        data.clear()  // Очищаем текущий список данных
        data.addAll(newChats)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = data[position]
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message, onChatClick)
            is OtherMessageViewHolder -> holder.bind(message, onChatClick)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = data[position]
        return if (message.UserId ==  userId) {
            TYPE_USER_MESSAGE
        } else {
            TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int = data.size
}