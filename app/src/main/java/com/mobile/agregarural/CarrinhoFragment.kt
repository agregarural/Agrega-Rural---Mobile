package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.databinding.FragmentCarrinhoBinding

class CarrinhoFragment : Fragment() {

    private var _binding: FragmentCarrinhoBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CarrinhoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarrinhoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = CarrinhoAdapter(
            itens = CarrinhoManager.itens,
            onExcluirClick = { item ->
                CarrinhoManager.removerProduto(item)
                adapter.notifyDataSetChanged()
            }
        )

        binding.recyclerCarrinho.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCarrinho.adapter = adapter

        CarrinhoManager.carregarCarrinho {
            adapter.notifyDataSetChanged()
        }

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFinalizarPedido.setOnClickListener {
            val selecionados = CarrinhoManager.itensSelecionados()

            if (selecionados.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Selecione pelo menos um item",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(R.id.telaFinalizacaoPedidoFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}