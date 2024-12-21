package com.lenincompany.mychat.ui.main.chats

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemChatBinding
import com.lenincompany.mychat.models.chat.ChatBody
import com.lenincompany.mychat.models.chat.Message
import com.squareup.picasso.Picasso

class ChatsRecyclerAdapter(
    private var data: MutableList<ChatBody>,
    private val onChatClick: (ChatBody) -> Unit,
) : RecyclerView.Adapter<ChatsRecyclerAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatBody, onChatClick: (ChatBody) -> Unit) {
            Log.d("ChatViewHolder", "Binding chat: ${chat.name}")
            val context = itemView.context
            if(chat.photo!=null)
                Picasso.get().load(chat.photo).into(binding.imageView)
            else
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
            binding.nameTv.text = chat.name
            binding.lastMessage.text =
                when (chat.lastMessage.Type)
                {
                    Message.TEXT -> chat.lastMessage.Content
                    Message.IMAGE -> "Фотокарточка"
                    Message.VIDEO -> "Видео"
                    Message.DOC -> "Документ"
                    else -> chat.lastMessage.Content
                }
            binding.root.setOnClickListener {
                onChatClick(chat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    fun setData(newChats: List<ChatBody>) {
        Log.d("ChatRecyclerAdapter", "Setting new data, size: ${newChats.size}")
        data.clear()  // Очищаем текущий список данных
        data.addAll(newChats)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        Log.d("ChatRecyclerAdapter", "Binding chat at position: $position")
        holder.bind(data[position], onChatClick)
    }

    override fun getItemCount(): Int = data.size
}