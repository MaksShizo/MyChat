package com.lenincompany.mychat.ui.chat

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.lenincompany.mychat.ui.chat.fullscreen.FullscreenImageFragment
import com.lenincompany.mychat.ui.chat.fullscreen.FullscreenVideoFragment
import com.lenincompany.mychat.utils.VideoSaver
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    class UserMessageViewHolder(private val binding: ItemUserMessageBinding, private val activity: AppCompatActivity) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            Log.d("UserMessageViewHolder", "Binding chat: ${message.Content} ${message.Type}")
            (activity as ChatActivity)

            when (message.Type) {
                Message.TEXT -> {
                    binding.messageTv.isVisible = true
                    binding.imageMessage.isGone = true
                    binding.videoView.isGone = true
                    binding.messageTv.text = message.Content
                }

                Message.IMAGE -> {
                    binding.messageTv.isGone = true
                    binding.imageMessage.isVisible = true
                    binding.videoView.isGone = true
                    Picasso.get().load(message.Content).into(binding.imageMessage)
                    binding.imageMessage.setOnClickListener {
                        activity.openFullscreenImage(message)
                    }
                }

                Message.VIDEO -> {
                    binding.messageTv.isGone = true
                    binding.imageMessage.isGone = true
                    binding.videoCL.isVisible = true
                    val videoSaver = VideoSaver()
                    var videoUri = videoSaver.getFileUriFromUrl(itemView.context, message.Content)
                    if (videoUri != null) {
                        binding.downloadBtn.isGone = true
                        binding.videoView.setVideoURI(videoUri)
                        binding.playBtn.isVisible = true
                    } else {
                        binding.downloadBtn.setOnClickListener {
                            activity.downloadVideoAndPlay(binding, message)
                        }
                    }
                    binding.videoView.setOnClickListener {
                        if (videoUri != null) {
                            activity.openFullscreenVideo(message, videoUri)
                        }
                    }
                    binding.playBtn.setOnClickListener {
                        if (videoUri != null) {
                            activity.playVideo(binding, videoUri)
                        }
                    }
                }

                Message.DOC -> {
                    binding.messageTv.isVisible = true
                    binding.imageMessage.isGone = true
                    binding.videoView.isGone = true
                    binding.messageTv.text = message.Content
                }
            }
        }
    }

    class OtherMessageViewHolder(
        private val binding: ItemOtherMessageBinding,
        private val users: List<ChatUsers>,
        private val usersPhoto: MutableList<UsersPhoto>,
        private val activity: AppCompatActivity
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message, onChatClick: (Message) -> Unit) {
            try {
                Log.d("OtherMessageViewHolder", "Binding chat: ${message.Content}")
                (activity as ChatActivity)
                val context = itemView.context
                binding.imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_app
                    )
                )
                binding.userName.text = users.find { it.UserId == message.UserId }!!.Name
                when (message.Type) {
                    Message.TEXT -> {
                        binding.messageTv.isVisible = true
                        binding.imageMessage.isGone = true
                        binding.videoView.isGone = true
                        binding.messageTv.text = message.Content
                    }

                    Message.IMAGE -> {
                        binding.messageTv.isGone = true
                        binding.imageMessage.isVisible = true
                        binding.videoView.isGone = true
                        Picasso.get().load(message.Content).into(binding.imageMessage)
                        binding.imageMessage.setOnClickListener {
                            activity.openFullscreenImage(message)
                        }
                    }

                    Message.VIDEO -> {
                        binding.messageTv.isGone = true
                        binding.imageMessage.isGone = true
                        binding.videoCL.isVisible = true
                        val videoSaver = VideoSaver()
                        var videoUri = videoSaver.getFileUriFromUrl(itemView.context, message.Content)
                        if (videoUri != null) {
                            binding.downloadBtn.isGone = true
                            binding.videoView.setVideoURI(videoUri)
                            binding.playBtn.isVisible = true
                        } else {
                            binding.downloadBtn.setOnClickListener {
                                activity.downloadVideoAndPlay(binding, message)
                            }
                        }
                        binding.videoView.setOnClickListener {
                            if (videoUri != null) {
                                activity.openFullscreenVideo(message, videoUri)
                            }
                        }
                        binding.playBtn.setOnClickListener {
                            if (videoUri != null) {
                                activity.playVideo(binding, videoUri)
                            }
                        }
                    }

                    Message.DOC -> {
                        binding.messageTv.isVisible = true
                        binding.imageMessage.isGone = true
                        binding.videoView.isGone = true
                        binding.messageTv.text = message.Content
                    }
                }

                val photo = usersPhoto.find { it.userId == message.UserId }
                if (photo != null)
                    binding.imageView.setImageBitmap(photo.bitmap)
            } catch (e: Exception) {
                Log.e("bindingError", "${e.message} USER: ${message.UserId} users: ${users.toString()}")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER_MESSAGE -> {
                val binding = ItemUserMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                UserMessageViewHolder(binding, parent.context as AppCompatActivity)
            }

            TYPE_OTHER_MESSAGE -> {
                val binding = ItemOtherMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                OtherMessageViewHolder(binding, users, usersPhoto, parent.context as AppCompatActivity)
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
        return if (message.UserId == userId) {
            TYPE_USER_MESSAGE
        } else {
            TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount(): Int = data.size
}