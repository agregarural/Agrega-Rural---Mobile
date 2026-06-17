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
import com.google.firebase.database.FirebaseDatabase
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