package com.mobile.agregarural

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentContatosCoopBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ContatosCoop : Fragment() {
    private var _binding: FragmentContatosCoopBinding? = null
    private val binding get() = _binding!!

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

        carregarDadosDaCooperativa()
        setupNavegacao()
    }

    private fun carregarDadosDaCooperativa() {
        val auth = FirebaseAuth.getInstance()
        val usuarioLogadoId = auth.currentUser?.uid

        if (usuarioLogadoId == null) {
            Toast.makeText(context, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance()
        database.getReference("usuarios").child(usuarioLogadoId)
            .get()
            .addOnSuccessListener { userSnapshot ->
                if (userSnapshot.exists()) {
                    val cooperativaId = userSnapshot.child("cooperativaId").getValue(String::class.java)
                    if (!cooperativaId.isNullOrEmpty()) {
                        buscarDadosNoFirebase(cooperativaId)
                    } else {
                        Toast.makeText(context, "Usuário não vinculado a uma cooperativa.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao buscar usuário: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun buscarDadosNoFirebase(cooperativaId: String) {
        val database = FirebaseDatabase.getInstance()

        database.getReference("Cooperativas").child(cooperativaId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val contatoSnapshot = snapshot.child("contato")
                    val telefone = contatoSnapshot.child("telefone").getValue(String::class.java) ?: "Não informado"
                    val email = contatoSnapshot.child("email").getValue(String::class.java) ?: "Não informado"

                    binding.txtTelefoneCoop.text = telefone
                    binding.txtEmailCoop.text = email


                    val endereco1Snapshot = snapshot.child("endereco")
                    if (endereco1Snapshot.exists()) {
                        val cep1 = endereco1Snapshot.child("cep").getValue(String::class.java) ?: ""
                        val numero1 = endereco1Snapshot.child("logradouro").getValue(String::class.java) ?: ""

                        if (cep1.isNotEmpty()) {
                            buscarEnderecoPorCep(cep1, numero1, 1)
                        }
                    } else {
                        binding.cardEndereco1.visibility = View.GONE
                    }

                    if (snapshot.hasChild("endereco2")) {
                        val endereco2Snapshot = snapshot.child("endereco2")
                        val cep2 = endereco2Snapshot.child("cep").getValue(String::class.java) ?: ""
                        val numero2 = endereco2Snapshot.child("logradouro").getValue(String::class.java) ?: ""

                        if (cep2.isNotEmpty()) {
                            binding.cardEndereco2.visibility = View.VISIBLE
                            buscarEnderecoPorCep(cep2, numero2, 2)
                        } else {
                            binding.cardEndereco2.visibility = View.GONE
                        }
                    } else {
                        binding.cardEndereco2.visibility = View.GONE
                    }

                }
            }
    }

    private fun buscarEnderecoPorCep(cep: String, numeroDoFirebase: String, tipoEndereco: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val urlLimpa = cep.replace("-", "").replace(".", "").trim()
                val url = URL("https://viacep.com.br/ws/$urlLimpa/json/")
                val conexao = url.openConnection() as HttpURLConnection
                conexao.requestMethod = "GET"

                if (conexao.responseCode == 200) {
                    val resposta = conexao.inputStream.bufferedReader().readText()
                    val json = JSONObject(resposta)

                    if (!json.has("erro")) {
                        val ruaViaCep = json.getString("logradouro")
                        val bairroViaCep = json.getString("bairro")
                        val cidadeViaCep = json.getString("localidade")
                        val ufViaCep = json.getString("uf")

                        val enderecoFormatado = "$ruaViaCep, $numeroDoFirebase – $bairroViaCep $cidadeViaCep – $ufViaCep, $cep"

                        //retorna para a Main Thread para atualizar a interface grafica
                        withContext(Dispatchers.Main) {
                            if (tipoEndereco == 1) {
                                binding.txtEndereco1Coop.text = enderecoFormatado
                            } else {
                                binding.txtEndereco2Coop.text = enderecoFormatado
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // fallback estruturado caso falte internet no momento da requisicao
                    val fallback = "CEP: $cep, Nº $numeroDoFirebase (Sem conexão para carregar detalhes)"
                    if (tipoEndereco == 1) binding.txtEndereco1Coop.text = fallback
                    else binding.txtEndereco2Coop.text = fallback
                }
            }
        }
    }

    private fun setupNavegacao() {
        binding.btnVoltar.setOnClickListener { findNavController().navigateUp() }
        binding.btnEntrega.setOnClickListener { findNavController().navigate(R.id.meusPedidosFragment) }
        binding.btnCarrinho.setOnClickListener { findNavController().navigate(R.id.carrinhoFragment) }
        binding.btnHome.setOnClickListener { findNavController().navigate(R.id.homeFragment) }
        binding.btnmenu.setOnClickListener { findNavController().navigate(R.id.menuFragment) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}