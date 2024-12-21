package com.lenincompany.mychat.ui.main.contacts

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemContactBinding
import com.lenincompany.mychat.models.user.UserInfoResponse

class ContactsRecyclerAdapter(
    private var data: MutableList<UserInfoResponse>
) : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactViewHolder>() {

    class ContactViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserInfoResponse) {
            Log.d("ContactViewHolder", "Binding Contact: ${user.Name}")
            val context = itemView.context
            binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
            binding.nameTv.text = user.Name
            binding.lastMessage.text = user.Phone
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(contacts: List<UserInfoResponse>) {
        Log.d("ContactRecyclerAdapter", "Setting new data, size: ${contacts.size}")
        data.clear()  // Очищаем текущий список данных
        data.addAll(contacts)  // Добавляем новые данные
        notifyDataSetChanged()  // Уведомляем адаптер, что данные изменились
    }
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        Log.d("ContactRecyclerAdapter", "Binding Contact at position: $position")
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size
}