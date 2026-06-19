package com.mobile.agregarural

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
import com.mobile.agregarural.databinding.FragmentTelaProdutoBinding

class TelaProduto : Fragment() {

    private var _binding: FragmentTelaProdutoBinding? = null
    private val binding get() = _binding!!

    private var usuarioEhCooperado: Boolean = false
    private var qntProduto = 1
    private var produtoAtual: Produto? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaProdutoBinding.inflate(inflater, container, false)

        produtoAtual = arguments?.getParcelable("produto", Produto::class.java)

        configurarProduto()
        configurarUsuarioPreco()
        configurarBotoes()

        return binding.root
    }

    private fun configurarUsuarioPreco() {
        PrecoUsuarioManager.verificarUsuarioEhCooperado { ehCooperado ->
            if (_binding == null) return@verificarUsuarioEhCooperado

            usuarioEhCooperado = ehCooperado
            atualizarValores()
        }
    }

    private fun configurarProduto() {
        val produto = produtoAtual

        if (produto != null) {
            binding.txtNameProduto.text = produto.nome
            binding.txtDescicao.text = produto.descricao
            binding.estoqueProduto.text = "Em estoque: ${produto.estoque}"
            binding.categoriaProduto.text = produto.categoria

            Glide.with(this)
                .load(produto.imagem)
                .into(binding.imgProduto)
        }

        atualizarValores()
    }

    private fun atualizarValores() {
        val produto = produtoAtual ?: return

        val precoFinal = PrecoUsuarioManager.precoFinal(produto, usuarioEhCooperado)
        val total = precoFinal * qntProduto

        binding.quantidadeProduto.text = qntProduto.toString()
        binding.precoProduto.text = "R$ %.2f".format(precoFinal)
        binding.valorTotalProduto.text = "Total: R$ %.2f".format(total)

        if (usuarioEhCooperado && produto.descontoCooperado > 0.0) {
            binding.txtPrecoNormalProduto.visibility = View.VISIBLE
            binding.txtDescontoCooperadoProduto.visibility = View.VISIBLE

            binding.txtPrecoNormalProduto.text = "Cliente normal: R$ %.2f".format(produto.preco)
            binding.txtDescontoCooperadoProduto.text =
                "Desconto cooperado: %.0f%%".format(produto.descontoCooperado)
        } else {
            binding.txtPrecoNormalProduto.visibility = View.GONE
            binding.txtDescontoCooperadoProduto.visibility = View.GONE
        }
    }

    private fun configurarBotoes() {
        binding.btnMais.setOnClickListener {
            val produto = produtoAtual

            if (produto != null) {
                if (qntProduto < produto.estoque) {
                    qntProduto++
                    atualizarValores()
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
                atualizarValores()
            }
        }

        binding.btnAdicionarCarrinho.setOnClickListener {
            val produto = produtoAtual

            if (produto != null) {
                CarrinhoManager.adicionarProduto(
                    produto = produto,
                    quantidade = qntProduto,
                    usuarioEhCooperado = usuarioEhCooperado
                )

                Toast.makeText(
                    requireContext(),
                    "Produto adicionado ao carrinho",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.carrinhoFragment)
            }
        }

        binding.btnComprar.setOnClickListener {
            val produto = produtoAtual

            if (produto != null) {
                CarrinhoManager.comprarAgora(
                    produto = produto,
                    quantidade = qntProduto,
                    usuarioEhCooperado = usuarioEhCooperado
                )

                findNavController().navigate(
                    R.id.action_telaProdutoFragment_to_telaFinalizacaoPedidoFragment
                )
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}