package com.mobile.agregarural

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentTelaProdutoBinding

import android.os.Parcelable
import com.bumptech.glide.Glide
import kotlinx.parcelize.Parcelize


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



        // Configurando funcionamento da tela //

        val produto = arguments?.getParcelable("produto", Produto::class.java)

        if (produto != null) {
            binding.txtNameProduto.text = produto.nome
            binding.precoProduto.text = "R$ ${produto.preco}"
            binding.txtDescicao.text = produto.descricao
            binding.estoqueProduto.text = "Em estoque: ${produto.estoque}"

            Glide.with(this)
                .load(produto.imagem)
                .into(binding.imgProduto)
        }




        // Configuração dos botões

        var qntProduto = 1
        binding.quantidadeProduto.text = qntProduto.toString()

        binding.btnMais.setOnClickListener {
            qntProduto++
            binding.quantidadeProduto.text = qntProduto.toString()
        }

        binding.btnMenos.setOnClickListener {
            if (qntProduto > 1) {
                qntProduto--
                binding.quantidadeProduto.text = qntProduto.toString()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnComprar.setOnClickListener {
            findNavController().navigate(
                R.id.action_telaProdutoFragment_to_telaFinalizacaoPedidoFragment
            )
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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}