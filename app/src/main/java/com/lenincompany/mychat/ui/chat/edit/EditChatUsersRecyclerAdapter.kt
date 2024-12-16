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
import com.lenincompany.mychat.models.chat.GroupChatUser
import com.lenincompany.mychat.models.chat.Message
import com.lenincompany.mychat.models.chat.UsersPhoto
import com.lenincompany.mychat.ui.main.contacts.ContactsRecyclerAdapter.ContactViewHolder

class EditChatUsersRecyclerAdapter(
    private var data: MutableList<GroupChatUser>,
    private val onChatClick: (Message) -> Unit,
    private val usersPhoto: MutableList<UsersPhoto>
) : RecyclerView.Adapter<EditChatUsersRecyclerAdapter.UserInChatViewHolder>() {
    class UserInChatViewHolder(private val binding: ItemUserInChatBinding,
                               private val usersPhoto: MutableList<UsersPhoto>) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: GroupChatUser, onChatClick: (Message) -> Unit) {
            val context = itemView.context
            binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
            binding.nameTv.text = user.user.Name
            binding.root.setOnClickListener {
            }
            val photo = usersPhoto.find { it.userId == user.userId }
            if(photo!=null)
                binding.imageView.setImageBitmap(photo.bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInChatViewHolder {
        val binding = ItemUserInChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserInChatViewHolder(binding, usersPhoto)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData( allUsers: List<GroupChatUser>) {
        data.clear()  // Очищаем текущий список данных
        data.addAll(allUsers)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addUsersPhoto(userPhoto: List<UsersPhoto>) {
        usersPhoto.addAll(userPhoto)
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }

    override fun onBindViewHolder(holder: UserInChatViewHolder, position: Int) {
        Log.d("ContactRecyclerAdapter", "Binding Contact at position: $position")
        holder.bind(data[position],onChatClick)
    }

    override fun getItemCount(): Int = data.size
}