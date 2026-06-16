package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobile.agregarural.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterCategoria: CategoriaAdapter
    private lateinit var adapterProdutos: ProdutoItemAdapter

    private val listaCategorias = mutableListOf<Categoria>()
    private val listaProdutos = mutableListOf<Produto>()

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carregarNomeUsuario()
        carregarFotoPerfil()

        configurarNavegacao()
        configurarRecyclerCategorias()
        configurarRecyclerProdutos()

        buscarCooperativaDoUsuario()
    }

    private fun buscarCooperativaDoUsuario() {
        val uid = usuarioId ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("coopUid")
            .get()
            .addOnSuccessListener { snapshot ->
                val idCooperativa = snapshot.getValue(String::class.java)

                if (!idCooperativa.isNullOrEmpty()) {
                    carregarCategoriasFirebase(idCooperativa)
                    carregarProdutosFirebase(idCooperativa)
                } else {
                    println("Usuário sem coopUid")
                }
            }
            .addOnFailureListener {
                println("Erro ao buscar coopUid")
            }
    }

    private fun configurarRecyclerCategorias() {
        adapterCategoria = CategoriaAdapter(listaCategorias)

        binding.rvCategorias.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvCategorias.adapter = adapterCategoria
    }

    private fun configurarRecyclerProdutos() {
        adapterProdutos = ProdutoItemAdapter(listaProdutos) { produtoClicado ->
            val bundle = Bundle().apply {
                putParcelable("produto", produtoClicado)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_telaProdutoFragment,
                bundle
            )
        }

        binding.rvProdutos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProdutos.adapter = adapterProdutos
    }

    private fun carregarCategoriasFirebase(idCooperativa: String) {
        val refCategorias = FirebaseDatabase.getInstance()
            .getReference("Cooperativas")
            .child(idCooperativa)
            .child("Categorias")

        refCategorias.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaCategorias.clear()

                for (categoriaSnapshot in snapshot.children) {
                    val categoria = categoriaSnapshot.getValue(Categoria::class.java)

                    if (categoria != null) {
                        listaCategorias.add(categoria)
                    }
                }

                adapterCategoria.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao carregar categorias: ${error.message}")
            }
        })
    }

    private fun carregarProdutosFirebase(idCooperativa: String) {
        val refProdutos = FirebaseDatabase.getInstance()
            .getReference("Cooperativas")
            .child(idCooperativa)
            .child("Produtos")

        refProdutos.addValueEventListener(object : ValueEventListener {
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
                println("Erro ao carregar produtos: ${error.message}")
            }
        })
    }

    private fun configurarNavegacao() {
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

        binding.imgPerfil.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }
    }

    private fun carregarFotoPerfil() {
        val uid = usuarioId ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("fotoPerfil")
            .get()
            .addOnSuccessListener { snapshot ->
                val urlImagem = snapshot.getValue(String::class.java)

                if (!urlImagem.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(urlImagem)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .circleCrop()
                        .into(binding.imgPerfil)
                }
            }
    }

    private fun carregarNomeUsuario() {
        val uid = usuarioId ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("nome")
            .get()
            .addOnSuccessListener { snapshot ->
                val nome = snapshot.getValue(String::class.java)

                binding.txtSaudacao.text = if (!nome.isNullOrEmpty()) {
                    "Olá, $nome"
                } else {
                    "Olá"
                }
            }
            .addOnFailureListener {
                binding.txtSaudacao.text = "Olá"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}