package com.lenincompany.mychat.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemOtherMessageBinding
import com.lenincompany.mychat.databinding.ItemUserMessageBinding
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.chat.UsersPhoto
import com.squareup.picasso.Picasso

class ChatRecyclerAdapter(
    private var data: MutableList<Message>,
    private val onChatClick: (Message) -> Unit,
    private val userId: Int,
    private val users: MutableList<ChatUsers>,
    private val usersPhoto: MutableList<UsersPhoto>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_USER_MESSAGE = 0
        const val TYPE_OTHER_MESSAGE = 1
    }

    // ViewHolder для сообщений пользователя
    class UserMessageViewHolder(private val binding: ItemUserMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            Log.d("UserMessageViewHolder", "Binding chat: ${message.Content} ${message.Type}")
            when(message.Type)
            {
                Message.TEXT ->
                {
                    binding.messageTv.isVisible = true
                    binding.imageMessage.isGone = true
                    binding.videoView.isGone = true
                    binding.messageTv.text = message.Content

                }
                Message.IMAGE ->
                {
                    binding.messageTv.isGone = true
                    binding.imageMessage.isVisible = true
                    binding.videoView.isGone = true
                    Picasso.get().load(message.Content).into(binding.imageMessage)
                }
                Message.VIDEO ->
                {
                    binding.messageTv.isGone = true
                    binding.imageMessage.isGone = true
                    binding.videoView.isVisible = true
                    //binding.videoView.setVideoPath(message.Content)
                }
                Message.DOC ->
                {
                    binding.messageTv.isVisible = true
                    binding.imageMessage.isGone = true
                    binding.videoView.isGone = true
                    binding.messageTv.text = message.Content
                }
            }
        }
    }

    // ViewHolder для сообщений собеседника
    class OtherMessageViewHolder(private val binding: ItemOtherMessageBinding,
                                 private val users: List<ChatUsers>,
                                 private val usersPhoto: MutableList<UsersPhoto>) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            try {
                Log.d("OtherMessageViewHolder", "Binding chat: ${message.Content}")
                val context = itemView.context
                binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
                binding.userName.text = users.find { it.UserId == message.UserId }!!.Name
                when(message.Type)
                {
                    Message.TEXT ->
                    {
                        binding.messageTv.isVisible = true
                        binding.imageMessage.isGone = true
                        binding.videoView.isGone = true
                        binding.messageTv.text = message.Content

                    }
                    Message.IMAGE ->
                    {
                        binding.messageTv.isGone = true
                        binding.imageMessage.isVisible = true
                        binding.videoView.isGone = true
                        Picasso.get().load(message.Content).into(binding.imageMessage)
                    }
                    Message.VIDEO ->
                    {
                        binding.messageTv.isGone = true
                        binding.imageMessage.isGone = true
                        binding.videoView.isVisible = true
                        //binding.videoView.setVideoPath(message.Content)
                    }
                    Message.DOC ->
                    {
                        binding.messageTv.isVisible = true
                        binding.imageMessage.isGone = true
                        binding.videoView.isGone = true
                        binding.messageTv.text = message.Content
                    }
                }
                val photo = usersPhoto.find { it.userId == message.UserId }
                if(photo!=null)
                    binding.imageView.setImageBitmap(photo.bitmap)
            }catch (e: Exception)
            {
                Log.e("bindingError", "${e.message} USER: ${message.UserId} users: ${users.toString()}")
            }
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
                OtherMessageViewHolder(binding, users, usersPhoto)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newChats: List<Message>, allUsers: List<ChatUsers>) {
        Log.d("ChatRecyclerAdapter", "Setting new data, size: ${newChats.size}")
        users.clear()
        users.addAll(allUsers)
        data.clear()  // Очищаем текущий список данных
        data.addAll(newChats)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addUsersPhoto(userPhoto: List<UsersPhoto>) {
        usersPhoto.clear()
        usersPhoto.addAll(userPhoto)
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