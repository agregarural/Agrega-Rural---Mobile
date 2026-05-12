package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.databinding.FragmentHomeBinding

/**
 * Fragment principal da Home.
 * Responsável por exibir a lista de categorias e a vitrine de produtos,
 * além de gerenciar a navegação básica através da barra inferior.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterCategoria: CategoriaAdapter
    private lateinit var adapterProdutos: ProdutoItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Inicializa o Fragment, configura os listeners dos botões de navegação
     * e os RecyclerViews de categorias e produtos utilizando dados do [MockDatabase].
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        binding.btnCarrinho.setOnClickListener {
            findNavController().navigate(R.id.carrinhoFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
        binding.btnperfil.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }


        // Configurando categorias usando MockDatabase
        val rvCategorias = binding.rvCategorias
        adapterCategoria = CategoriaAdapter(MockDatabase.categorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategorias.adapter = adapterCategoria

        // Configurando vitrine de produtos usando MockDatabase
        val rvProdutos = binding.rvProdutos
        adapterProdutos = ProdutoItemAdapter(MockDatabase.produtos) { produtoClicado ->
            findNavController().navigate(R.id.action_homeFragment_to_telaProdutoFragment)
        }

        rvProdutos.layoutManager = GridLayoutManager(requireContext(), 2)
        rvProdutos.adapter = adapterProdutos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}