package com.lenincompany.mychat.ui.chat.edit

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemContactBinding
import com.lenincompany.mychat.databinding.ItemOtherMessageBinding
import com.lenincompany.mychat.databinding.ItemUserInChatBinding
import com.lenincompany.mychat.databinding.ItemUserMessageBinding
import com.lenincompany.mychat.models.chat.ChatUsers
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.chat.UsersPhoto
import com.lenincompany.mychat.ui.main.contacts.ContactsRecyclerAdapter.ContactViewHolder

class EditChatUsersRecyclerAdapter(
    private var data: MutableList<ChatUsers>,
    private val onChatClick: (Message) -> Unit,
    private val usersPhoto: MutableList<UsersPhoto>
) : RecyclerView.Adapter<EditChatUsersRecyclerAdapter.UserInChatViewHolder>() {
    class UserInChatViewHolder(private val binding: ItemUserInChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ChatUsers, onChatClick: (Message) -> Unit) {
            val context = itemView.context
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInChatViewHolder {
        val binding = ItemUserInChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserInChatViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData( allUsers: List<ChatUsers>) {
        data.clear()  // Очищаем текущий список данных
        data.addAll(allUsers)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addUsersPhoto(userPhoto: UsersPhoto) {
        usersPhoto.add(userPhoto)
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }

    override fun onBindViewHolder(holder: UserInChatViewHolder, position: Int) {
        Log.d("ContactRecyclerAdapter", "Binding Contact at position: $position")
        holder.bind(data[position],onChatClick)
    }

    override fun getItemCount(): Int = data.size
}