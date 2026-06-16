package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentTelaPagamentoBinding

class TelaPagamento : Fragment() {

    private var _binding: FragmentTelaPagamentoBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPagamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFinalizar.setOnClickListener {
            finalizarPedido()
        }

        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnCarrinho.setOnClickListener {
            findNavController().navigate(R.id.carrinhoFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
    }

    private fun finalizarPedido() {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        val pedidoId = PedidoManager.pedidoAtualId

        if (pedidoId == null) {
            Toast.makeText(
                requireContext(),
                "Pedido não encontrado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uid = usuarioAtual.uid

        val updates = hashMapOf<String, Any>(
            "/Pedidos/$pedidoId/status" to "em andamento",
            "/Pedidos/$pedidoId/pago" to true,
            "/Usuarios/$uid/Pedidos/$pedidoId/status" to "em andamento",
            "/Usuarios/$uid/Pedidos/$pedidoId/pago" to true
        )

        database.updateChildren(updates)
            .addOnSuccessListener {
                removerItensSelecionadosDoCarrinho()

                Toast.makeText(
                    requireContext(),
                    "Pedido finalizado com sucesso",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.meusPedidosFragment)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao finalizar pedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun removerItensSelecionadosDoCarrinho() {
        val uid = auth.currentUser?.uid ?: return
        val selecionados = CarrinhoManager.itensSelecionados().toList()

        for (item in selecionados) {
            database.child("Usuarios")
                .child(uid)
                .child("Carrinho")
                .child(item.produto.nome)
                .removeValue()

            CarrinhoManager.itens.remove(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}