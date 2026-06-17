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
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        val pedidoId = PedidoManager.pedidoAtualId

        if (pedidoId == null) {
            Toast.makeText(requireContext(), "Pedido não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("Usuarios")
            .child(uid)
            .child("Pedidos")
            .child(pedidoId)
            .get()
            .addOnSuccessListener { snapshot ->

                valorPedidoAtual = getDouble(snapshot, "valorTotal")

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
                Toast.makeText(requireContext(), "Erro ao carregar pedido", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarPixNoPedido(pedidoId: String, uid: String) {
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

        val clip = ClipData.newPlainText("Código Pix", codigoPixAtual)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Código Pix copiado", Toast.LENGTH_SHORT).show()
    }

    private fun finalizarPedido() {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        val pedidoId = PedidoManager.pedidoAtualId

        if (pedidoId == null) {
            Toast.makeText(requireContext(), "Pedido não encontrado", Toast.LENGTH_SHORT).show()
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

                val vendaRegistrada = pedidoSnapshot
                    .child("vendaRegistrada")
                    .getValue(Boolean::class.java) ?: false

                if (statusAtual != "pendente") {
                    Toast.makeText(
                        requireContext(),
                        "Esse pedido já foi finalizado",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                if (vendaRegistrada) {
                    Toast.makeText(
                        requireContext(),
                        "Venda já registrada para esse pedido",
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

                        prepararVendaDiminuirEstoqueEMarcarPedido(
                            uid = uid,
                            pedidoId = pedidoId,
                            coopUid = coopUid,
                            pedidoSnapshot = pedidoSnapshot
                        )
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao buscar pedido", Toast.LENGTH_SHORT).show()
            }
    }

    private fun prepararVendaDiminuirEstoqueEMarcarPedido(
        uid: String,
        pedidoId: String,
        coopUid: String,
        pedidoSnapshot: DataSnapshot
    ) {
        val itensSnapshot = pedidoSnapshot.child("itens")

        if (!itensSnapshot.exists()) {
            Toast.makeText(requireContext(), "Pedido sem itens", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("Cooperativas")
            .child(coopUid)
            .child("Produtos")
            .get()
            .addOnSuccessListener { produtosSnapshot ->

                var receitaTotalVenda = 0.0
                var custoTotalVenda = 0.0

                val itensVenda = mutableListOf<Map<String, Any>>()

                for (itemSnapshot in itensSnapshot.children) {
                    val nomeProdutoPedido = itemSnapshot
                        .child("nome")
                        .getValue(String::class.java) ?: ""

                    val quantidadeComprada = getInt(itemSnapshot, "quantidade")

                    val precoUnitarioPedido =
                        getDouble(itemSnapshot, "precoUnitario")
                            .takeIf { it > 0.0 }
                            ?: getDouble(itemSnapshot, "preco")

                    val imagemProduto = itemSnapshot
                        .child("imagem")
                        .getValue(String::class.java) ?: ""

                    val categoriaProduto = itemSnapshot
                        .child("categoria")
                        .getValue(String::class.java) ?: ""

                    if (nomeProdutoPedido.isBlank() || quantidadeComprada <= 0) {
                        continue
                    }

                    var produtoEncontrado: DataSnapshot? = null

                    for (produtoSnapshot in produtosSnapshot.children) {
                        val nomeProdutoBanco = produtoSnapshot
                            .child("nome")
                            .getValue(String::class.java) ?: ""

                        if (normalizar(nomeProdutoBanco) == normalizar(nomeProdutoPedido)) {
                            produtoEncontrado = produtoSnapshot
                            break
                        }
                    }

                    if (produtoEncontrado != null) {
                        val produtoId = produtoEncontrado.key ?: continue

                        val custoUnitario = getDouble(produtoEncontrado, "custo")
                        val precoBanco = getDouble(produtoEncontrado, "preco")

                        val precoFinal = if (precoUnitarioPedido > 0.0) {
                            precoUnitarioPedido
                        } else {
                            precoBanco
                        }

                        val receitaItem = precoFinal * quantidadeComprada
                        val custoItem = custoUnitario * quantidadeComprada
                        val lucroItem = receitaItem - custoItem

                        receitaTotalVenda += receitaItem
                        custoTotalVenda += custoItem

                        itensVenda.add(
                            mapOf(
                                "produtoId" to produtoId,
                                "nome" to nomeProdutoPedido,
                                "quantidade" to quantidadeComprada,
                                "precoUnitario" to precoFinal,
                                "custoUnitario" to custoUnitario,
                                "receitaItem" to receitaItem,
                                "custoItem" to custoItem,
                                "lucroItem" to lucroItem,
                                "imagem" to imagemProduto,
                                "categoria" to categoriaProduto
                            )
                        )

                        diminuirEstoqueProduto(
                            coopUid = coopUid,
                            produtoId = produtoId,
                            quantidadeComprada = quantidadeComprada
                        )
                    }
                }

                if (itensVenda.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Nenhum produto da cooperativa foi encontrado no pedido",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                registrarVendaEMarcarPedidoEmAndamento(
                    uid = uid,
                    pedidoId = pedidoId,
                    coopUid = coopUid,
                    receitaTotalVenda = receitaTotalVenda,
                    custoTotalVenda = custoTotalVenda,
                    itensVenda = itensVenda
                )
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao atualizar estoque", Toast.LENGTH_SHORT).show()
            }
    }

    private fun diminuirEstoqueProduto(
        coopUid: String,
        produtoId: String,
        quantidadeComprada: Int
    ) {
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

    private fun registrarVendaEMarcarPedidoEmAndamento(
        uid: String,
        pedidoId: String,
        coopUid: String,
        receitaTotalVenda: Double,
        custoTotalVenda: Double,
        itensVenda: List<Map<String, Any>>
    ) {
        val lucroTotalVenda = receitaTotalVenda - custoTotalVenda
        val dataVendaMillis = System.currentTimeMillis()

        val venda = hashMapOf<String, Any>(
            "vendaId" to pedidoId,
            "pedidoId" to pedidoId,
            "usuarioId" to uid,
            "coopUid" to coopUid,
            "valorReceita" to receitaTotalVenda,
            "valorCusto" to custoTotalVenda,
            "valorLucro" to lucroTotalVenda,
            "dataVendaMillis" to dataVendaMillis,
            "valorPago" to valorPedidoAtual,
            "codigoPix" to codigoPixAtual,
            "itens" to itensVenda
        )

        val updates = hashMapOf<String, Any>(
            "/Vendas/$pedidoId" to venda,
            "/Cooperativas/$coopUid/Vendas/$pedidoId" to venda,

            "/Pedidos/$pedidoId/status" to "em andamento",
            "/Pedidos/$pedidoId/pago" to true,
            "/Pedidos/$pedidoId/vendaRegistrada" to true,
            "/Pedidos/$pedidoId/codigoPix" to codigoPixAtual,
            "/Pedidos/$pedidoId/valorPago" to valorPedidoAtual,

            "/Usuarios/$uid/Pedidos/$pedidoId/status" to "em andamento",
            "/Usuarios/$uid/Pedidos/$pedidoId/pago" to true,
            "/Usuarios/$uid/Pedidos/$pedidoId/vendaRegistrada" to true,
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

    private fun getDouble(snapshot: DataSnapshot, campo: String): Double {
        val valor = snapshot.child(campo).value

        return when (valor) {
            is Double -> valor
            is Long -> valor.toDouble()
            is Int -> valor.toDouble()
            is Float -> valor.toDouble()
            is String -> valor.replace(",", ".").toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun getInt(snapshot: DataSnapshot, campo: String): Int {
        val valor = snapshot.child(campo).value

        return when (valor) {
            is Int -> valor
            is Long -> valor.toInt()
            is Double -> valor.toInt()
            is Float -> valor.toInt()
            is String -> valor.toIntOrNull() ?: 0
            else -> 0
        }
    }

    private fun normalizar(texto: String): String {
        return texto.trim().lowercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}