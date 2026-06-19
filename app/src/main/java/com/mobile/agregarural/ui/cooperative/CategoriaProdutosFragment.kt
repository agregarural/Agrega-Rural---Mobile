package com.mobile.agregarural.ui.cooperative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.R
import com.mobile.agregarural.data.manager.PrecoUsuarioManager
import com.mobile.agregarural.data.model.Produto
import com.mobile.agregarural.databinding.FragmentCategoriaProdutosBinding
import com.mobile.agregarural.ui.product.ProdutoItemAdapter

class CategoriaProdutosFragment : Fragment() {

    private var _binding: FragmentCategoriaProdutosBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterProdutos: ProdutoItemAdapter

    private val listaProdutosCategoria = mutableListOf<Produto>()

    private var categoriaSelecionada: String = ""
    private var usuarioEhCooperado: Boolean = false

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriaProdutosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriaSelecionada = arguments?.getString("CATEGORIA") ?: ""

        binding.txtTituloCategoria.text = categoriaSelecionada

        configurarNavegacao()

        PrecoUsuarioManager.verificarUsuarioEhCooperado { ehCooperado ->
            if (_binding == null) return@verificarUsuarioEhCooperado

            usuarioEhCooperado = ehCooperado

            configurarRecyclerProdutos()
            buscarCooperativaDoUsuario()
        }
    }

    private fun configurarRecyclerProdutos() {
        adapterProdutos = ProdutoItemAdapter(
            list = listaProdutosCategoria,
            usuarioEhCooperado = usuarioEhCooperado
        ) { produtoClicado ->

            val bundle = Bundle().apply {
                putParcelable("produto", produtoClicado)
            }

            findNavController().navigate(
                R.id.telaProdutoFragment,
                bundle
            )
        }

        binding.rvProdutosCategoria.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProdutosCategoria.adapter = adapterProdutos
    }

    private fun buscarCooperativaDoUsuario() {
        val uid = usuarioId

        if (uid == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("coopUid")
            .get()
            .addOnSuccessListener { snapshot ->
                val coopUid = snapshot.getValue(String::class.java)

                if (coopUid.isNullOrBlank()) {
                    Toast.makeText(requireContext(), "Cooperativa não encontrada", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                carregarProdutosDaCategoria(coopUid)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao buscar cooperativa", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarProdutosDaCategoria(coopUid: String) {
        FirebaseDatabase.getInstance()
            .getReference("Cooperativas")
            .child(coopUid)
            .child("Produtos")
            .get()
            .addOnSuccessListener { snapshot ->

                listaProdutosCategoria.clear()

                for (produtoSnapshot in snapshot.children) {
                    val produto = produtoSnapshot.getValue(Produto::class.java)

                    if (produto != null) {
                        if (produto.categoria.equals(categoriaSelecionada, ignoreCase = true)) {
                            listaProdutosCategoria.add(produto)
                        }
                    }
                }

                adapterProdutos.notifyDataSetChanged()

                if (listaProdutosCategoria.isEmpty()) {
                    binding.txtSemProdutos.visibility = View.VISIBLE
                } else {
                    binding.txtSemProdutos.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao carregar produtos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarNavegacao() {
        binding.btnVoltar.setOnClickListener {
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