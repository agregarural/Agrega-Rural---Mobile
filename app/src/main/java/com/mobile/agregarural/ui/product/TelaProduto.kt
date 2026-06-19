package com.mobile.agregarural.ui.product

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mobile.agregarural.data.manager.CarrinhoManager
import com.mobile.agregarural.data.model.Produto
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentTelaProdutoBinding

class TelaProduto : Fragment() {

    private var _binding: FragmentTelaProdutoBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaProdutoBinding.inflate(inflater, container, false)

        val produto = arguments?.getParcelable("produto", Produto::class.java)

        var qntProduto = 1

        fun atualizarTotal() {
            val precoUnitario = produto?.preco ?: 0.0
            val total = precoUnitario * qntProduto

            binding.quantidadeProduto.text = qntProduto.toString()
            binding.valorTotalProduto.text = "Total: R$ %.2f".format(total)
        }

        if (produto != null) {
            binding.txtNameProduto.text = produto.nome
            binding.precoProduto.text = "R$ %.2f".format(produto.preco)
            binding.txtDescicao.text = produto.descricao
            binding.estoqueProduto.text = "Em estoque: ${produto.estoque}"
            binding.categoriaProduto.text = produto.categoria

            Glide.with(this)
                .load(produto.imagem)
                .into(binding.imgProduto)
        }

        atualizarTotal()

        binding.btnMais.setOnClickListener {
            if (produto != null) {
                if (qntProduto < produto.estoque) {
                    qntProduto++
                    atualizarTotal()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Quantidade máxima em estoque: ${produto.estoque}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnMenos.setOnClickListener {
            if (qntProduto > 1) {
                qntProduto--
                atualizarTotal()
            }
        }

        binding.btnAdicionarCarrinho.setOnClickListener {
            if (produto != null) {
                CarrinhoManager.adicionarProduto(produto, qntProduto)

                Toast.makeText(
                    requireContext(),
                    "Produto adicionado ao carrinho",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.carrinhoFragment)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnComprar.setOnClickListener {
            if (produto != null) {
                CarrinhoManager.comprarAgora(produto, qntProduto)

                findNavController().navigate(
                    R.id.action_telaProdutoFragment_to_telaFinalizacaoPedidoFragment
                )
            }
        }

        binding.btnAdicionarCarrinho.setOnClickListener {
            if (produto != null) {
                CarrinhoManager.adicionarProduto(produto, qntProduto)

                Toast.makeText(
                    requireContext(),
                    "Produto adicionado ao carrinho",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.carrinhoFragment)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}