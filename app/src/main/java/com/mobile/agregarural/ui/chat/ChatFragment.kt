package com.mobile.agregarural.ui.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.data.model.ChatAdapter
import com.mobile.agregarural.data.model.ChatMessage
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentChatBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatFragment : Fragment(R.layout.fragment_chat) {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter
    private val messageList = mutableListOf<ChatMessage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChatBinding.bind(view)

        setupRecyclerView()
        setupListeners()

        if (messageList.isEmpty()) {
            addMessage(getString(R.string.mensagem_boasvindas), isBot = true)
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                addMessage(text, isBot = false)
                binding.etMessage.text.clear()
                triggerBotResponse(text)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun addMessage(text: String, isBot: Boolean) {
        messageList.add(ChatMessage(text, isBot))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        binding.rvChat.scrollToPosition(messageList.size - 1)
    }

    private fun triggerBotResponse(userMessage: String) {
        lifecycleScope.launch {
            delay(1000)
            val response = when {
                userMessage.contains("telefone", true) -> getString(R.string.mensagem1) +
                        getString(R.string.mensagem2) +
                        getString(R.string.mensagem2) +
                        "\n" +
                        getString(R.string.mensagem3)
                userMessage.contains("olá", true) -> getString(R.string.mensagem4)
                userMessage.contains("pedido", true) -> getString(R.string.mensagem5)
                else -> getString(R.string.mensagem6) +
                        "\n" +
                        getString(R.string.mensagem7)
            }
            addMessage(response, isBot = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}