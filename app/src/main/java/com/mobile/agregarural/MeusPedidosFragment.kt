package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentMeusPedidosBinding

class MeusPedidosFragment : Fragment() {

    private var _binding: FragmentMeusPedidosBinding? = null
    private val binding get() = _binding!!

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

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
        carregarFotoPerfil()
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

        binding.imgPerfil.setOnClickListener {
            findNavController().navigate(R.id.perfilFragment)
        }
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