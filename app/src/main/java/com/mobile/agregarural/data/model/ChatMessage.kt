package com.mobile.agregarural.data.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.agregarural.databinding.ItemChatBotBinding
import com.mobile.agregarural.databinding.ItemChatUserBinding

data class ChatMessage(val text: String, val isBot: Boolean)

class ChatAdapter(private val list: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_USER = 1
    private val TYPE_BOT = 2

    override fun getItemViewType(position: Int): Int {
        return if (list[position].isBot) TYPE_BOT else TYPE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_BOT) {
            val binding = ItemChatBotBinding.inflate(inflater, parent, false)
            BotViewHolder(binding)
        } else {
            val binding = ItemChatUserBinding.inflate(inflater, parent, false)
            UserViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]
        if (holder is BotViewHolder) {
            holder.binding.tvMessageBot.text = message.text // Use o ID do seu XML
        } else if (holder is UserViewHolder) {
            holder.binding.tvMessageUser.text = message.text // Use o ID do seu XML
        }
    }

    override fun getItemCount(): Int = list.size

    class BotViewHolder(val binding: ItemChatBotBinding) : RecyclerView.ViewHolder(binding.root)
    class UserViewHolder(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
}