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
import com.bumptech.glide.Glide
import com.mobile.agregarural.databinding.FragmentHomeBinding

import com.google.firebase.auth.FirebaseAuth
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

    private val sliderHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private lateinit var sliderRunnable: Runnable

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

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


        carregarNomeUsuario()
        carregarFotoPerfil()




        //lista de Imagens do Carrosel
        val listaBanners = listOf(
            R.drawable.anuncio1,
            R.drawable.anuncio1,
            R.drawable.anuncio1
        )

        val viewPager = binding.viewPagerBanners
        viewPager.adapter = BannerAdapter(listaBanners)

        com.google.android.material.tabs.TabLayoutMediator(binding.tabLayoutIndicator, viewPager) { _, _ ->
        }.attach()

        sliderRunnable = Runnable {
            val totalItems = viewPager.adapter?.itemCount ?: 0
            if (totalItems > 0) {
                val nextItem = (viewPager.currentItem + 1) % totalItems
                viewPager.currentItem = nextItem
            }
        }

        //reinicia o temporizador toda vez que o usuário arrastar com o dedo
        viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 4000)
            }
        })





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

    private fun carregarFotoPerfil() {
        val uid = usuarioId

        if (uid == null) return

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
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)

        ref.child("nome").get()
            .addOnSuccessListener { snapshot ->
                val nome = snapshot.getValue(String::class.java)

                if (!nome.isNullOrEmpty()) {
                    binding.txtSaudacao.text = "Olá, $nome"
                } else {
                    binding.txtSaudacao.text = "Olá"
                }
            }
            .addOnFailureListener {
                binding.txtSaudacao.text = "OLÁ"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sliderHandler.removeCallbacks(sliderRunnable)
        _binding = null
    }
}