package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentTelaProdutoBinding


class TelaProduto : Fragment() {

    private var _binding: FragmentTelaProdutoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaProdutoBinding.inflate(inflater, container, false)

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