package com.mobile.agregarural

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        // Mensagem de boas-vindas do Bot
        if (messageList.isEmpty()) {
            addMessage("Olá! Bem-vindo ao suporte. Como posso ajudar?", isBot = true)
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
        // Simulação simples de lógica de bot
        lifecycleScope.launch {
            delay(1000) // Simula o bot "digitando"
            val response = when {
                userMessage.contains("telefone", true) -> "Se preferir falar com a nossa equipe por telefone, você pode ligar para:\n" +
                        "(xx) xxxx-xxxx\n" +
                        "(xx) xxxx-xxxx\n" +
                        "\n" +
                        "Horário de atendimento: Segunda a sexta-feira, das 8h às 18h Sábados, das 8h às 12h"
                userMessage.contains("olá", true) -> "Olá! Digite sua dúvida sobre pedidos ou perfil."
                userMessage.contains("pedido", true) -> "Você pode rastrear seu pedido na tela 'Entrega'."
                else -> "Entendi. Vou encaminhar sua dúvida para um especialista.\n" +
                        "\n" +
                        "Ao ser analisado. Enviaremos uma mensagem para o email informado."
            }
            addMessage(response, isBot = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}