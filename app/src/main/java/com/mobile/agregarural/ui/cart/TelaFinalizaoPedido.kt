package com.mobile.agregarural.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.data.manager.CarrinhoManager
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentTelaFinalizacaoPedidoBinding

class TelaFinalizaoPedido : Fragment() {

    private var _binding: FragmentTelaFinalizacaoPedidoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTelaFinalizacaoPedidoBinding.inflate(inflater, container, false)

        val listaItens = CarrinhoManager.itensSelecionados()

        val resumo = StringBuilder()
        var precoFinal = 0.0

        if (listaItens.isEmpty()) {
            resumo.append("Nenhum item selecionado.")
        } else {
            listaItens.forEach { item ->

                val produto = item.produto
                val subtotal = produto.preco * item.quantidade
                precoFinal += subtotal

                resumo.append("Produto: ${produto.nome}\n")
                resumo.append("Preço: R$ %.2f\n".format(produto.preco))
                resumo.append("Quantidade: ${item.quantidade}\n")
                resumo.append("Subtotal: R$ %.2f\n".format(subtotal))
                resumo.append("______________________________________\n\n")
            }
        }

        binding.pedidos.text = resumo.toString()
        binding.totalPedidos.text = "TOTAL R$ %.2f".format(precoFinal)

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnConfirmar.setOnClickListener {
            findNavController().navigate(
                R.id.action_telaFinalizacaoPedidoFragment_to_telaPagamentoEnderecoFragment
            )
        }

        binding.btnCarrinho.setOnClickListener {
            findNavController().navigate(R.id.carrinhoFragment)
        }

        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}