package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentDadosPessoaisBinding

class DadosPessoaisFragment : Fragment() {

    private var _binding: FragmentDadosPessoaisBinding? = null
    private val binding get() = _binding!!

    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDadosPessoaisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarFotoPerfil()
        carregarNomeUsuario()
        carregarCPFUsuario()
        carregarCoopUsuario()
        carregarCodigoUsuario()
        setupClickListeners()
    }

    private fun carregarCPFUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)

        ref.child("cpf").get()
            .addOnSuccessListener { snapshot ->
                val cpf = snapshot.getValue(String::class.java)
                if (!cpf.isNullOrEmpty()) {
                    binding.tvCpf.text = "$cpf"
                }
            }

    }

    private fun carregarCodigoUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)

        ref.child("matricula").get()
            .addOnSuccessListener { snapshot ->
                val matricula = snapshot.getValue(String::class.java)
                if (!matricula.isNullOrEmpty()) {
                    binding.tvCodigoMembro.text = "$matricula"
                }
            }

    }

    private fun carregarCoopUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)

        ref.child("cooperativa").get()
            .addOnSuccessListener { snapshot ->
                val cooperativa = snapshot.getValue(String::class.java)
                if (!cooperativa.isNullOrEmpty()) {
                    binding.tvCooperativa.text = "$cooperativa"
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
                    binding.tvNome.text = "$nome"
                }
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
                        .into(binding.imgPerfil)
                }
            }
    }



    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardAlterarFoto.setOnClickListener {
            abrirSeletorDeFoto()
        }



        setupBottomNavigation()
    }

    private fun abrirSeletorDeFoto() {
        Toast.makeText(requireContext(), "Alterar foto de perfil", Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
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


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}