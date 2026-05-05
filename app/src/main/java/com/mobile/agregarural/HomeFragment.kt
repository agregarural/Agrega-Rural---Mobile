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

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



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


        val database = Firebase.database
        val myRef = database.getReference("message")
        myRef.setValue("Hello, World!")







        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
        binding.btnperfil.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }


        val listaProdutos = mutableListOf<Produto>()


        // Configurando categorias usando MockDatabase
        val rvCategorias = binding.rvCategorias
        adapterCategoria = CategoriaAdapter(MockDatabase.categorias)
        rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCategorias.adapter = adapterCategoria

        // Configurando vitrine de produtos usando MockDatabase
        val rvProdutos = binding.rvProdutos

        adapterProdutos = ProdutoItemAdapter(listaProdutos) { produtoClicado ->
            val bundle = Bundle().apply {
                putParcelable("produto", produtoClicado)
            }
            findNavController().navigate(R.id.action_homeFragment_to_telaProdutoFragment, bundle)
        }

        rvProdutos.layoutManager = GridLayoutManager(requireContext(), 2)
        rvProdutos.adapter = adapterProdutos

        val ref = FirebaseDatabase.getInstance()
            .getReference("Cooperativas")
            .child("01")
            .child("Produtos")



        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                listaProdutos.clear()

                for (produtoSnapshot in snapshot.children) {
                    val produto = produtoSnapshot.getValue(Produto::class.java)

                    if (produto != null) {
                        listaProdutos.add(produto)
                    }
                }

                adapterProdutos.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro Firebase: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}