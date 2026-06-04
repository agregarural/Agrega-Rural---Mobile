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
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentTelaPagamentoEnderecoBinding

class TelaPagamentoEndereco : Fragment() {

    private var _binding: FragmentTelaPagamentoEnderecoBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private lateinit var adapter: EnderecoPagamentoAdapter
    private val enderecos = mutableListOf<Endereco>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPagamentoEnderecoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()
        carregarEnderecosDoFirebase()
        setupClickListeners()
    }

    private fun configurarRecyclerView() {
        adapter = EnderecoPagamentoAdapter(enderecos)

        binding.rvEnderecosSalvos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEnderecosSalvos.adapter = adapter
    }

    private fun carregarEnderecosDoFirebase() {
        val usuarioAtual = auth.currentUser ?: return

        database.child("Usuarios")
            .child(usuarioAtual.uid)
            .child("enderecos")
            .get()
            .addOnSuccessListener { snapshot ->
                enderecos.clear()

                for (enderecoSnapshot in snapshot.children) {
                    val endereco = enderecoSnapshot.getValue(Endereco::class.java)

                    if (endereco != null) {
                        enderecos.add(endereco)
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar endereços",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnConfirmarNewAdress.setOnClickListener {
            salvarEnderecoNoRealtimeDatabase()
        }

        binding.btnConfirmar.setOnClickListener {
            val enderecoSelecionado = adapter.enderecoSelecionado

            if (enderecoSelecionado == null) {
                Toast.makeText(
                    requireContext(),
                    "Selecione um endereço",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            findNavController().navigate(
                R.id.action_telaPagamentoEnderecoFragment_to_telaPagamentoFragment
            )
        }

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
    }

    private fun salvarEnderecoNoRealtimeDatabase() {
        val usuarioAtual = auth.currentUser

        if (usuarioAtual == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        val nome = binding.edtNomeEndereco.text.toString().trim()
        val cep = binding.edtCep.text.toString().trim()
        val numero = binding.edtNumero.text.toString().trim()
        val logradouro = binding.edtLogradouro.text.toString().trim()
        val complemento = binding.edtComplemento.text.toString().trim()
        val referencia = binding.edtReferencia.text.toString().trim()

        if (nome.isBlank() || cep.isBlank() || numero.isBlank() || logradouro.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Preencha nome, CEP, número e logradouro",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uid = usuarioAtual.uid

        val enderecoId = database
            .child("Usuarios")
            .child(uid)
            .child("enderecos")
            .push()
            .key

        if (enderecoId == null) {
            Toast.makeText(
                requireContext(),
                "Erro ao gerar ID do endereço",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val endereco = Endereco(
            id = enderecoId,
            nome = nome,
            cep = cep,
            numero = numero,
            logradouro = logradouro,
            complemento = complemento,
            referencia = referencia,
            criadoEm = System.currentTimeMillis()
        )

        database.child("Usuarios")
            .child(uid)
            .child("enderecos")
            .child(enderecoId)
            .setValue(endereco)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Endereço cadastrado com sucesso",
                    Toast.LENGTH_SHORT
                ).show()

                limparCampos()
                carregarEnderecosDoFirebase()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao cadastrar endereço",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun limparCampos() {
        binding.edtNomeEndereco.text?.clear()
        binding.edtCep.text?.clear()
        binding.edtNumero.text?.clear()
        binding.edtLogradouro.text?.clear()
        binding.edtComplemento.text?.clear()
        binding.edtReferencia.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}