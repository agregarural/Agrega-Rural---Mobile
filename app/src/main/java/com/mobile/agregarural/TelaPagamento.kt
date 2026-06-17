package com.mobile.agregarural

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.mobile.agregarural.databinding.FragmentTelaPagamentoBinding

class TelaPagamento : Fragment() {

    private var _binding: FragmentTelaPagamentoBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val chavePixRecebedor = "16088933755"
    private val nomeRecebedor = "AGREGA RURAL"
    private val cidadeRecebedor = "VILA VELHA"

    private var valorPedidoAtual: Double = 0.0
    private var codigoPixAtual: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPagamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        carregarPedidoEGerarPix()

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCopiarPix.setOnClickListener {
            copiarCodigoPix()
        }

        binding.edtCodigoPix.setOnClickListener {
            copiarCodigoPix()
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

    private fun carregarPedidoEGerarPix() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(
                requireContext(),
                "Usuário não logado",
                Toast.LENGTH_SHORT
            ).show()
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

        database.child("Usuarios")
            .child(uid)
            .child("Pedidos")
            .child(pedidoId)
            .get()
            .addOnSuccessListener { snapshot ->

                valorPedidoAtual = snapshot
                    .child("valorTotal")
                    .getValue(Double::class.java) ?: 0.0

                if (valorPedidoAtual <= 0.0) {
                    Toast.makeText(
                        requireContext(),
                        "Valor do pedido inválido",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                val txid = PixUtils.limparTxid(pedidoId)

                codigoPixAtual = PixUtils.gerarPixCopiaECola(
                    chavePix = chavePixRecebedor,
                    nomeRecebedor = nomeRecebedor,
                    cidadeRecebedor = cidadeRecebedor,
                    valor = valorPedidoAtual,
                    txid = txid
                )

                binding.edtCodigoPix.setText(codigoPixAtual)

                val qrCode = PixUtils.gerarQrCode(codigoPixAtual)
                binding.imgQrCodePix.setImageBitmap(qrCode)

                salvarPixNoPedido(pedidoId, uid)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar pedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun salvarPixNoPedido(
        pedidoId: String,
        uid: String
    ) {
        val updates = hashMapOf<String, Any>(
            "/Pedidos/$pedidoId/codigoPix" to codigoPixAtual,
            "/Pedidos/$pedidoId/valorPix" to valorPedidoAtual,
            "/Usuarios/$uid/Pedidos/$pedidoId/codigoPix" to codigoPixAtual,
            "/Usuarios/$uid/Pedidos/$pedidoId/valorPix" to valorPedidoAtual
        )

        database.updateChildren(updates)
    }

    private fun copiarCodigoPix() {
        if (codigoPixAtual.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Código Pix ainda não foi gerado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val clipboard = requireContext()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText(
            "Código Pix",
            codigoPixAtual
        )

        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            requireContext(),
            "Código Pix copiado",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun finalizarPedido() {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(
                requireContext(),
                "Usuário não logado",
                Toast.LENGTH_SHORT
            ).show()
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

        database.child("Usuarios")
            .child(uid)
            .child("Pedidos")
            .child(pedidoId)
            .get()
            .addOnSuccessListener { pedidoSnapshot ->

                val statusAtual = pedidoSnapshot
                    .child("status")
                    .getValue(String::class.java) ?: ""

                if (statusAtual != "pendente") {
                    Toast.makeText(
                        requireContext(),
                        "Esse pedido já foi finalizado",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                database.child("Usuarios")
                    .child(uid)
                    .child("coopUid")
                    .get()
                    .addOnSuccessListener { coopSnapshot ->

                        val coopUid = coopSnapshot.getValue(String::class.java)

                        if (coopUid.isNullOrBlank()) {
                            Toast.makeText(
                                requireContext(),
                                "Cooperativa do usuário não encontrada",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@addOnSuccessListener
                        }

                        diminuirEstoqueDosProdutos(
                            uid = uid,
                            pedidoId = pedidoId,
                            coopUid = coopUid,
                            pedidoSnapshot = pedidoSnapshot
                        )
                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao buscar pedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun diminuirEstoqueDosProdutos(
        uid: String,
        pedidoId: String,
        coopUid: String,
        pedidoSnapshot: DataSnapshot
    ) {
        val itensSnapshot = pedidoSnapshot.child("itens")

        if (!itensSnapshot.exists()) {
            Toast.makeText(
                requireContext(),
                "Pedido sem itens",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        database.child("Cooperativas")
            .child(coopUid)
            .child("Produtos")
            .get()
            .addOnSuccessListener { produtosSnapshot ->

                val tarefas = mutableListOf<com.google.android.gms.tasks.Task<Void>>()

                for (itemSnapshot in itensSnapshot.children) {
                    val nomeProdutoPedido = itemSnapshot
                        .child("nome")
                        .getValue(String::class.java) ?: ""

                    val quantidadeComprada = itemSnapshot
                        .child("quantidade")
                        .getValue(Int::class.java) ?: 0

                    if (nomeProdutoPedido.isBlank() || quantidadeComprada <= 0) {
                        continue
                    }

                    var produtoEncontrado: DataSnapshot? = null

                    for (produtoSnapshot in produtosSnapshot.children) {
                        val nomeProdutoBanco = produtoSnapshot
                            .child("nome")
                            .getValue(String::class.java) ?: ""

                        if (nomeProdutoBanco == nomeProdutoPedido) {
                            produtoEncontrado = produtoSnapshot
                            break
                        }
                    }

                    if (produtoEncontrado != null) {
                        val produtoId = produtoEncontrado.key ?: continue

                        val estoqueRef = database
                            .child("Cooperativas")
                            .child(coopUid)
                            .child("Produtos")
                            .child(produtoId)
                            .child("estoque")

                        estoqueRef.runTransaction(object : Transaction.Handler {
                            override fun doTransaction(currentData: MutableData): Transaction.Result {
                                val estoqueAtual = currentData.getValue(Int::class.java) ?: 0

                                val novoEstoque = estoqueAtual - quantidadeComprada

                                currentData.value = if (novoEstoque < 0) {
                                    0
                                } else {
                                    novoEstoque
                                }

                                return Transaction.success(currentData)
                            }

                            override fun onComplete(
                                error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?
                            ) {
                            }
                        })
                    }
                }

                marcarPedidoComoEmAndamento(uid, pedidoId)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao atualizar estoque",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun marcarPedidoComoEmAndamento(
        uid: String,
        pedidoId: String
    ) {
        val updates = hashMapOf<String, Any>(
            "/Pedidos/$pedidoId/status" to "em andamento",
            "/Pedidos/$pedidoId/pago" to true,
            "/Pedidos/$pedidoId/codigoPix" to codigoPixAtual,
            "/Pedidos/$pedidoId/valorPago" to valorPedidoAtual,

            "/Usuarios/$uid/Pedidos/$pedidoId/status" to "em andamento",
            "/Usuarios/$uid/Pedidos/$pedidoId/pago" to true,
            "/Usuarios/$uid/Pedidos/$pedidoId/codigoPix" to codigoPixAtual,
            "/Usuarios/$uid/Pedidos/$pedidoId/valorPago" to valorPedidoAtual
        )

        database.updateChildren(updates)
            .addOnSuccessListener {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}