package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentMeusPedidosBinding

class MeusPedidosFragment : Fragment() {

    private var _binding: FragmentMeusPedidosBinding? = null
    private val binding get() = _binding!!

    private val pedidosConcluidos = mutableListOf<PedidoUsuario>()
    private val pedidosAndamento = mutableListOf<PedidoUsuario>()
    private val pedidosPendentes = mutableListOf<PedidoUsuario>()

    private lateinit var adapterConcluidos: PedidoAdapter
    private lateinit var adapterAndamento: PedidoAdapter
    private lateinit var adapterPendentes: PedidoAdapter

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusPedidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        carregarFotoPerfil()
        setupNavigation()
        setupRecyclerViews()
        carregarPedidosDoFirebase()
    }

    private fun setupNavigation() {
        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }

        binding.btnCarrinho.setOnClickListener {
            findNavController().navigate(R.id.carrinhoFragment)
        }

        binding.imgPerfil.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }
    }

    private fun carregarFotoPerfil() {
        val uid = usuarioId ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("fotoPerfil")
            .get()
            .addOnSuccessListener { snapshot ->
                val urlImagem = snapshot.getValue(String::class.java)

                if (!urlImagem.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(urlImagem)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .circleCrop()
                        .into(binding.imgPerfil)
                }
            }
    }

    private fun setupRecyclerViews() {
        adapterConcluidos = PedidoAdapter(
            pedidos = pedidosConcluidos,
            onPagarClick = { pedido ->
                irParaPagamento(pedido)
            },
            onPedidoAlterado = {
                carregarPedidosDoFirebase()
            }
        )

        adapterAndamento = PedidoAdapter(
            pedidos = pedidosAndamento,
            onPagarClick = { pedido ->
                irParaPagamento(pedido)
            },
            onPedidoAlterado = {
                carregarPedidosDoFirebase()
            }
        )

        adapterPendentes = PedidoAdapter(
            pedidos = pedidosPendentes,
            onPagarClick = { pedido ->
                irParaPagamento(pedido)
            },
            onPedidoAlterado = {
                carregarPedidosDoFirebase()
            }
        )

        binding.rvPedidosConcluidos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPedidosConcluidos.adapter = adapterConcluidos

        binding.rvPedidosAndamento.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPedidosAndamento.adapter = adapterAndamento

        binding.rvPedidosPendentes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPedidosPendentes.adapter = adapterPendentes
    }

    private fun carregarPedidosDoFirebase() {
        val uid = usuarioId ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Pedidos")
            .get()
            .addOnSuccessListener { snapshot ->

                pedidosConcluidos.clear()
                pedidosAndamento.clear()
                pedidosPendentes.clear()

                for (pedidoSnapshot in snapshot.children) {
                    val pedidoId = pedidoSnapshot.child("pedidoId").getValue(String::class.java)
                        ?: pedidoSnapshot.key
                        ?: ""

                    var status = pedidoSnapshot.child("status").getValue(String::class.java)
                        ?: "pendente"

                    val valorTotal = pedidoSnapshot.child("valorTotal").getValue(Double::class.java)
                        ?: 0.0

                    val dataPedido = pedidoSnapshot.child("dataPedido").getValue(String::class.java)
                        ?: ""

                    val dataEntrega = pedidoSnapshot.child("dataEntrega").getValue(String::class.java)
                        ?: ""

                    val dataPedidoMillis = pedidoSnapshot.child("dataPedidoMillis").getValue(Long::class.java)
                        ?: 0L

                    val dataEntregaMillis = pedidoSnapshot.child("dataEntregaMillis").getValue(Long::class.java)
                        ?: 0L

                    val pago = pedidoSnapshot.child("pago").getValue(Boolean::class.java)
                        ?: false

                    if (
                        status == "em andamento" &&
                        dataEntregaMillis > 0L &&
                        System.currentTimeMillis() > dataEntregaMillis
                    ) {
                        status = "concluido"
                        atualizarStatusAutomaticamente(pedidoId)
                    }

                    val itens = mutableListOf<ItemPedidoUsuario>()

                    for (itemSnapshot in pedidoSnapshot.child("itens").children) {
                        val item = ItemPedidoUsuario(
                            nome = itemSnapshot.child("nome").getValue(String::class.java) ?: "",
                            precoUnitario = itemSnapshot.child("precoUnitario").getValue(Double::class.java) ?: 0.0,
                            quantidade = itemSnapshot.child("quantidade").getValue(Int::class.java) ?: 0,
                            subtotal = itemSnapshot.child("subtotal").getValue(Double::class.java) ?: 0.0,
                            imagem = itemSnapshot.child("imagem").getValue(String::class.java) ?: "",
                            categoria = itemSnapshot.child("categoria").getValue(String::class.java) ?: "",
                            descricao = itemSnapshot.child("descricao").getValue(String::class.java) ?: ""
                        )

                        itens.add(item)
                    }

                    val pedido = PedidoUsuario(
                        pedidoId = pedidoId,
                        status = status,
                        valorTotal = valorTotal,
                        dataPedido = dataPedido,
                        dataEntrega = dataEntrega,
                        dataPedidoMillis = dataPedidoMillis,
                        dataEntregaMillis = dataEntregaMillis,
                        pago = pago,
                        itens = itens
                    )

                    when (status.lowercase()) {
                        "concluido" -> pedidosConcluidos.add(pedido)
                        "em andamento" -> pedidosAndamento.add(pedido)
                        "pendente" -> pedidosPendentes.add(pedido)
                    }
                }

                pedidosConcluidos.sortByDescending { it.dataPedidoMillis }
                pedidosAndamento.sortByDescending { it.dataPedidoMillis }
                pedidosPendentes.sortByDescending { it.dataPedidoMillis }

                adapterConcluidos.notifyDataSetChanged()
                adapterAndamento.notifyDataSetChanged()
                adapterPendentes.notifyDataSetChanged()
            }
    }

    private fun irParaPagamento(pedido: PedidoUsuario) {
        PedidoManager.pedidoAtualId = pedido.pedidoId
        findNavController().navigate(R.id.telaPagamentoFragment)
    }

    private fun atualizarStatusAutomaticamente(pedidoId: String) {
        val uid = usuarioId ?: return

        val updates = hashMapOf<String, Any>(
            "/Pedidos/$pedidoId/status" to "concluido",
            "/Usuarios/$uid/Pedidos/$pedidoId/status" to "concluido"
        )

        FirebaseDatabase.getInstance()
            .reference
            .updateChildren(updates)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}