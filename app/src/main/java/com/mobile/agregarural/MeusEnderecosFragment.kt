package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobile.agregarural.databinding.FragmentMeusEnderecosBinding

class MeusEnderecosFragment : Fragment() {

    private var _binding: FragmentMeusEnderecosBinding? = null
    private val binding get() = _binding!!

    private lateinit var enderecoAdapter: EnderecoAdapter

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var listenerEnderecos: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusEnderecosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        carregarEnderecosDoUsuario()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        enderecoAdapter = EnderecoAdapter(
            listaEnderecos = mutableListOf(),
            onEnderecoClick = { endereco ->
                Toast.makeText(
                    requireContext(),
                    "Endereço selecionado:\n${endereco.logradouro}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onEditarClick = { endereco ->
                Toast.makeText(
                    requireContext(),
                    "Editar endereço: ${endereco.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerEnderecos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = enderecoAdapter
        }
    }

    private fun carregarEnderecosDoUsuario() {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = usuarioAtual.uid

        val referenciaEnderecos = database
            .child("Usuarios")
            .child(uid)
            .child("enderecos")

        listenerEnderecos = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Endereco>()

                for (enderecoSnapshot in snapshot.children) {
                    val endereco = enderecoSnapshot.getValue(Endereco::class.java)

                    if (endereco != null) {
                        endereco.id = enderecoSnapshot.key ?: ""
                        lista.add(endereco)
                    }
                }

                lista.sortBy { it.criadoEm }

                enderecoAdapter.atualizarLista(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar endereços: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        referenciaEnderecos.addValueEventListener(listenerEnderecos!!)
    }

    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.adicionar.setOnClickListener {
            findNavController().navigate(R.id.telaPagamentoEnderecoFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val usuarioAtual = auth.currentUser

        if (usuarioAtual != null && listenerEnderecos != null) {
            database
                .child("Usuarios")
                .child(usuarioAtual.uid)
                .child("enderecos")
                .removeEventListener(listenerEnderecos!!)
        }

        _binding = null
    }
}