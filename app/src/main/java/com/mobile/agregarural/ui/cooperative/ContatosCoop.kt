package com.mobile.agregarural.ui.cooperative

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentContatosCoopBinding

class ContatosCoop : Fragment() {

    private var _binding: FragmentContatosCoopBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContatosCoopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtTelefoneCoop.text = getString(R.string.carregando)
        binding.txtEmailCoop.text = getString(R.string.carregando)
        binding.txtEndereco1Coop.text = getString(R.string.carregando)
        binding.txtEndereco2Coop.text = getString(R.string.carregando)

        carregarCooperativaDoUsuario()
        setupNavegacao()
    }

    private fun carregarCooperativaDoUsuario() {
        val uidUsuario = auth.currentUser?.uid

        if (uidUsuario == null) {
            Toast.makeText(
                requireContext(),
                getString(R.string.usuario_nao_autenticado),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        database.child("Usuarios")
            .child(uidUsuario)
            .child("coopUid")
            .get()
            .addOnSuccessListener { snapshot ->

                val coopUid = snapshot.getValue(String::class.java)

                if (coopUid.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.usuario_nao_vinculado_a_uma_cooperativa),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                buscarContatoEEnderecoDaCooperativa(coopUid)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_ao_buscar_coopuid_do_usuario),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun buscarContatoEEnderecoDaCooperativa(coopUid: String) {
        database.child("Cooperativas")
            .child(coopUid)
            .get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.cooperativa_nao_encontrada),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                carregarContato(snapshot.child("contato"))
                carregarEnderecoPrincipal(snapshot.child("endereco"))
                carregarEnderecoSecundario(snapshot.child("endereco2"))
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.erro_ao_carregar_dados_da_cooperativa),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun carregarContato(contatoSnapshot: DataSnapshot) {
        val telefone = contatoSnapshot.child("telefone").getValue(String::class.java)
            ?: getString(R.string.nao_informado)

        val email = contatoSnapshot.child("email").getValue(String::class.java)
            ?: getString(R.string.nao_informado)

        binding.txtTelefoneCoop.text = telefone
        binding.txtEmailCoop.text = email
    }

    private fun carregarEnderecoPrincipal(enderecoSnapshot: DataSnapshot) {
        if (!enderecoSnapshot.exists()) {
            binding.cardEndereco1.visibility = View.GONE
            return
        }

        binding.cardEndereco1.visibility = View.VISIBLE
        binding.txtEndereco1Coop.text = montarEndereco(enderecoSnapshot)
    }

    private fun carregarEnderecoSecundario(enderecoSnapshot: DataSnapshot) {
        if (!enderecoSnapshot.exists()) {
            binding.cardEndereco2.visibility = View.GONE
            return
        }

        binding.cardEndereco2.visibility = View.VISIBLE
        binding.txtEndereco2Coop.text = montarEndereco(enderecoSnapshot)
    }

    private fun montarEndereco(enderecoSnapshot: DataSnapshot): String {
        val logradouro = enderecoSnapshot.child("logradouro").getValue(String::class.java) ?: ""
        val cidade = enderecoSnapshot.child("cidade").getValue(String::class.java) ?: ""
        val estado = enderecoSnapshot.child("estado").getValue(String::class.java) ?: ""
        val cep = enderecoSnapshot.child("cep").getValue(String::class.java) ?: ""

        val partes = mutableListOf<String>()

        if (logradouro.isNotBlank()) {
            partes.add(logradouro)
        }

        if (cidade.isNotBlank() && estado.isNotBlank()) {
            partes.add("$cidade - $estado")
        } else if (cidade.isNotBlank()) {
            partes.add(cidade)
        } else if (estado.isNotBlank()) {
            partes.add(estado)
        }

        if (cep.isNotBlank()) {
            partes.add("CEP: $cep")
        }

        return if (partes.isEmpty()) {
            getString(R.string.endereco_nao_informado)
        } else {
            partes.joinToString(", ")
        }
    }

    private fun setupNavegacao() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}