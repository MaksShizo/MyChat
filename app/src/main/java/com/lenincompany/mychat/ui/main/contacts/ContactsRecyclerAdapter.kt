package com.lenincompany.mychat.ui.main.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lenincompany.mychat.R
import com.lenincompany.mychat.databinding.ItemContactBinding
import com.lenincompany.mychat.models.Contact

class ContactsRecyclerAdapter(
    private var data: MutableList<Contact>
) : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactViewHolder>() {

    class ContactViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            Log.d("ContactViewHolder", "Binding Contact: ${contact.name}")
            val context = itemView.context
            binding.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_app))
            binding.nameTv.text = contact.name
            binding.lastMessage.text = contact.phone
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

    fun setData(contacts: List<Contact>) {
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