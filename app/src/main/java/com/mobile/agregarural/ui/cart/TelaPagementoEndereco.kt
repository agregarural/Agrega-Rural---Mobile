package com.mobile.agregarural.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.data.manager.CarrinhoManager
import com.mobile.agregarural.data.model.Endereco
import com.mobile.agregarural.data.model.ItemCarrinhos
import com.mobile.agregarural.data.manager.PedidoManager
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentTelaPagamentoEnderecoBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TelaPagamentoEndereco : Fragment() {

    private var _binding: FragmentTelaPagamentoEnderecoBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private lateinit var adapter: EnderecoPagamentoAdapter
    private val enderecos = mutableListOf<Endereco>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPagamentoEnderecoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()
        carregarEnderecosDoFirebase()
        setupClickListeners()
    }

    private fun configurarRecyclerView() {
        adapter = EnderecoPagamentoAdapter(enderecos)

        binding.rvEnderecosSalvos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEnderecosSalvos.adapter = adapter
    }

    private fun carregarEnderecosDoFirebase() {
        val usuarioAtual = auth.currentUser ?: return

        database.child("Usuarios")
            .child(usuarioAtual.uid)
            .child("enderecos")
            .get()
            .addOnSuccessListener { snapshot ->
                enderecos.clear()

                for (enderecoSnapshot in snapshot.children) {
                    val endereco = enderecoSnapshot.getValue(Endereco::class.java)

                    if (endereco != null) {
                        enderecos.add(endereco)
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_ao_carregar_enderecos),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnConfirmarNewAdress.setOnClickListener {
            salvarEnderecoNoRealtimeDatabase()
        }

        binding.btnConfirmar.setOnClickListener {
            val enderecoSelecionado = adapter.enderecoSelecionado

            if (enderecoSelecionado == null) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selecione_um_endereco),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            criarPedidoPendente(enderecoSelecionado)
        }
    }

    private fun criarPedidoPendente(enderecoSelecionado: Endereco) {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.usuario_nao_logado),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val itensSelecionados = CarrinhoManager.itensSelecionados()

        if (itensSelecionados.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.nenhum_item_selecionado_para_o_pedido),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uid = usuarioAtual.uid

        database.child("Usuarios")
            .child(uid)
            .child("coopUid")
            .get()
            .addOnSuccessListener { coopSnapshot ->

                val coopUid = coopSnapshot.getValue(String::class.java) ?: ""

                if (coopUid.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cooperativa_do_usuario_nao_encontrada),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                criarPedidoNoFirebase(
                    uid = uid,
                    coopUid = coopUid,
                    enderecoSelecionado = enderecoSelecionado,
                    itensSelecionados = itensSelecionados
                )
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_ao_buscar_cooperativa_do_usuario),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun criarPedidoNoFirebase(
        uid: String,
        coopUid: String,
        enderecoSelecionado: Endereco,
        itensSelecionados: List<ItemCarrinhos>
    ) {
        val pedidoId = database
            .child("Pedidos")
            .push()
            .key

        if (pedidoId == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.erro_ao_gerar_pedido),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dataPedidoMillis = System.currentTimeMillis()
        val doisDiasMillis = 2 * 24 * 60 * 60 * 1000L
        val dataEntregaMillis = dataPedidoMillis + doisDiasMillis

        val formatoData = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )

        val itensPedido = itensSelecionados.map { item ->
            val produto = item.produto
            val precoUnitarioUsado = item.precoUnitario
            val subtotal = precoUnitarioUsado * item.quantidade

            mapOf(
                "nome" to produto.nome,
                "categoria" to produto.categoria,
                "descricao" to produto.descricao,
                "imagem" to produto.imagem,

                "quantidade" to item.quantidade,
                "precoUnitario" to precoUnitarioUsado,
                "subtotal" to subtotal,

                "precoNormal" to produto.preco,
                "precoCooperado" to produto.precoCooperado,
                "descontoCooperado" to produto.descontoCooperado,
                "usuarioCooperado" to item.usuarioCooperado,

                "custo" to produto.custo,
                "estoqueNoMomento" to produto.estoque
            )
        }

        val valorTotal = itensSelecionados.sumOf { item ->
            item.precoUnitario * item.quantidade
        }

        val enderecoPedido = mapOf(
            "id" to enderecoSelecionado.id,
            "nome" to enderecoSelecionado.nome,
            "cep" to enderecoSelecionado.cep,
            "numero" to enderecoSelecionado.numero,
            "logradouro" to enderecoSelecionado.logradouro,
            "complemento" to enderecoSelecionado.complemento,
            "referencia" to enderecoSelecionado.referencia
        )

        val pedido = hashMapOf<String, Any>(
            "pedidoId" to pedidoId,
            "usuarioId" to uid,
            "coopUid" to coopUid,

            "status" to "pendente",
            "pago" to false,
            "vendaRegistrada" to false,

            "valorTotal" to valorTotal,

            "dataPedidoMillis" to dataPedidoMillis,
            "dataEntregaMillis" to dataEntregaMillis,
            "dataPedido" to formatoData.format(Date(dataPedidoMillis)),
            "dataEntrega" to formatoData.format(Date(dataEntregaMillis)),

            "endereco" to enderecoPedido,
            "itens" to itensPedido
        )

        val updates = hashMapOf<String, Any>(
            "/Pedidos/$pedidoId" to pedido,
            "/Usuarios/$uid/Pedidos/$pedidoId" to pedido
        )

        database.updateChildren(updates)
            .addOnSuccessListener {
                PedidoManager.pedidoAtualId = pedidoId

                CarrinhoManager.removerItensSelecionadosDoCarrinhoFirebase()

                Toast.makeText(
                    requireContext(),
                    getString(R.string.pedido_criado_aguardando_pagamento),
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(
                    R.id.action_telaPagamentoEnderecoFragment_to_telaPagamentoFragment
                )
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_ao_criar_pedido),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun salvarEnderecoNoRealtimeDatabase() {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.usuario_nao_logado),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val nome = binding.edtNomeEndereco.text.toString().trim()
        val cep = binding.edtCep.text.toString().trim()
        val numero = binding.edtNumero.text.toString().trim()
        val logradouro = binding.edtLogradouro.text.toString().trim()
        val complemento = binding.edtComplemento.text.toString().trim()
        val referencia = binding.edtReferencia.text.toString().trim()

        if (nome.isBlank() || cep.isBlank() || numero.isBlank() || logradouro.isBlank()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.preencha_nome_cep_numero_e_logradouro),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uid = usuarioAtual.uid

        val enderecoId = database
            .child("Usuarios")
            .child(uid)
            .child("enderecos")
            .push()
            .key

        if (enderecoId == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.erro_ao_gerar_id_do_endereco),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val endereco = Endereco(
            id = enderecoId,
            nome = nome,
            cep = cep,
            numero = numero,
            logradouro = logradouro,
            complemento = complemento,
            referencia = referencia,
            criadoEm = System.currentTimeMillis()
        )

        database.child("Usuarios")
            .child(uid)
            .child("enderecos")
            .child(enderecoId)
            .setValue(endereco)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.endereco_cadastrado_com_sucesso),
                    Toast.LENGTH_SHORT
                ).show()

                limparCampos()
                carregarEnderecosDoFirebase()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_ao_cadastrar_endereco),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun limparCampos() {
        binding.edtNomeEndereco.text?.clear()
        binding.edtCep.text?.clear()
        binding.edtNumero.text?.clear()
        binding.edtLogradouro.text?.clear()
        binding.edtComplemento.text?.clear()
        binding.edtReferencia.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}