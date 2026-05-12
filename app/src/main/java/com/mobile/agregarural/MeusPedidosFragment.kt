package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.databinding.FragmentMeusPedidosBinding

class MeusPedidosFragment : Fragment() {

    private var _binding: FragmentMeusPedidosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusPedidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupRecyclerViews()
    }

    private fun setupNavigation() {
        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
        binding.btnCarrinho.setOnClickListener {
            findNavController().navigate(R.id.carrinhoFragment)
        }

        binding.btnperfil.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }
    }

    private fun setupRecyclerViews() {
        val produtosFake = MockDatabase.produtos
        val pedidosFake = MockDatabase.pedidos

        binding.rvUltimosPedidos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvUltimosPedidos.adapter = ProdutoAdapter(produtosFake)

        binding.rvListaPedidos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListaPedidos.adapter = PedidoAdapter(pedidosFake)

        binding.rvSugestoes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSugestoes.adapter = ProdutoAdapter(produtosFake)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}