package com.mobile.agregarural

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobile.agregarural.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterCategoria: CategoriaAdapter
    private lateinit var adapterProdutos: ProdutoItemAdapter
    private lateinit var adapterBanner: BannerAdapter

    private val listaCategorias = mutableListOf<Categoria>()

    // Lista que aparece na tela
    private val listaProdutos = mutableListOf<Produto>()

    // Lista completa, usada para fazer a busca
    private val listaProdutosCompleta = mutableListOf<Produto>()

    private val listaBanners = mutableListOf<Banner>()

    private var usuarioEhCooperado = false

    private val handlerBanner = Handler(Looper.getMainLooper())

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            if (_binding != null && listaBanners.size > 1) {
                val proximaPosicao =
                    (binding.bannerCarousel.currentItem + 1) % listaBanners.size

                binding.bannerCarousel.setCurrentItem(proximaPosicao, true)
            }

            handlerBanner.postDelayed(this, 3000)
        }
    }

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
        configurarCarouselBanner()
        configurarBuscador()

        verificarPrecoDoUsuarioECarregarProdutos()
    }

    private fun verificarPrecoDoUsuarioECarregarProdutos() {
        PrecoUsuarioManager.verificarUsuarioEhCooperado { ehCooperado ->
            if (_binding == null) return@verificarUsuarioEhCooperado

            usuarioEhCooperado = ehCooperado

            configurarRecyclerProdutos()
            buscarCooperativaDoUsuario()
        }
    }

    private fun configurarBuscador() {
        binding.searchBar.queryHint = "Buscar produtos..."

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarProdutos(query.orEmpty())
                binding.searchBar.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarProdutos(newText.orEmpty())
                return true
            }
        })
    }

    private fun filtrarProdutos(textoBusca: String) {
        val busca = textoBusca.trim().lowercase()

        listaProdutos.clear()

        if (busca.isEmpty()) {
            listaProdutos.addAll(listaProdutosCompleta)
        } else {
            val produtosFiltrados = listaProdutosCompleta.filter { produto ->
                produto.nome.lowercase().contains(busca) ||
                        produto.categoria.lowercase().contains(busca) ||
                        produto.descricao.lowercase().contains(busca)
            }

            listaProdutos.addAll(produtosFiltrados)
        }

        if (::adapterProdutos.isInitialized) {
            adapterProdutos.notifyDataSetChanged()
        }
    }

    private fun configurarCarouselBanner() {
        adapterBanner = BannerAdapter(listaBanners)

        binding.bannerCarousel.adapter = adapterBanner
        binding.bannerCarousel.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.dotsBanner, binding.bannerCarousel) { _, _ -> }.attach()
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
                    carregarBannersFirebase(idCooperativa)
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

    private fun carregarBannersFirebase(idCooperativa: String) {
        val refBanners = FirebaseDatabase.getInstance()
            .getReference("Cooperativas")
            .child(idCooperativa)
            .child("Banners")

        refBanners.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaBanners.clear()

                for (bannerSnapshot in snapshot.children) {
                    val banner = bannerSnapshot.getValue(Banner::class.java)

                    if (banner != null && banner.url.isNotEmpty()) {
                        listaBanners.add(banner)
                    }
                }

                adapterBanner.notifyDataSetChanged()

                if (listaBanners.size > 1) {
                    handlerBanner.removeCallbacks(autoSlideRunnable)
                    handlerBanner.postDelayed(autoSlideRunnable, 3000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Erro ao carregar banners: ${error.message}")
            }
        })
    }

    private fun configurarRecyclerCategorias() {
        adapterCategoria = CategoriaAdapter(listaCategorias)

        binding.rvCategorias.layoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rvCategorias.adapter = adapterCategoria
    }

    private fun configurarRecyclerProdutos() {
        adapterProdutos = ProdutoItemAdapter(
            list = listaProdutos,
            usuarioEhCooperado = usuarioEhCooperado
        ) { produtoClicado ->

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
                listaProdutosCompleta.clear()
                listaProdutos.clear()

                for (produtoSnapshot in snapshot.children) {
                    val produto = produtoSnapshot.getValue(Produto::class.java)

                    if (produto != null) {
                        listaProdutosCompleta.add(produto)
                    }
                }

                val textoAtualBusca = binding.searchBar.query?.toString().orEmpty()

                if (textoAtualBusca.isBlank()) {
                    listaProdutos.addAll(listaProdutosCompleta)
                    adapterProdutos.notifyDataSetChanged()
                } else {
                    filtrarProdutos(textoAtualBusca)
                }
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
        handlerBanner.removeCallbacks(autoSlideRunnable)
        _binding = null
    }
}