package com.lenincompany.mychat.ui.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemChatBinding
import com.lenincompany.mychat.models.ChatBody
import com.lenincompany.mychat.models.Message

class ChatRecyclerAdapter(
    private var data: MutableList<Message>,
    private val onChatClick: (Message) -> Unit,
) : RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            Log.d("ChatViewHolder", "Binding chat: ${message.Content}")
            val context = itemView.context
            binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
            binding.nameTv.text = message.Content
            binding.root.setOnClickListener {
                onChatClick(message)
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

    fun setData(newChats: List<Message>) {
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