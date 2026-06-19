package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentMinhaCooperativaBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MinhaCooperativa : Fragment() {

    private var _binding: FragmentMinhaCooperativaBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var coopUidAtual: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMinhaCooperativaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carregarDadosCooperativa()
        configurarCliques()
    }

    private fun carregarDadosCooperativa() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(
                requireContext(),
                "Usuário não logado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        database.child("Usuarios")
            .child(uid)
            .child("coopUid")
            .get()
            .addOnSuccessListener { snapshotUsuario ->

                val coopUid = snapshotUsuario.getValue(String::class.java)

                if (coopUid.isNullOrBlank()) {
                    Toast.makeText(
                        requireContext(),
                        "Cooperativa não encontrada para este usuário",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                coopUidAtual = coopUid

                buscarCooperativa(coopUid)
                contarCooperados(coopUid)
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao buscar cooperativa do usuário",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun buscarCooperativa(coopUid: String) {
        database.child("Cooperativas")
            .child(coopUid)
            .get()
            .addOnSuccessListener { snapshot ->

                val logoUrl = snapshot.child("logoURL").getValue(String::class.java) ?: ""

                val estadosAtuacao =
                    snapshot.child("estadosAtuacao").getValue(Int::class.java)
                        ?: snapshot.child("estadoAtuacao").getValue(Int::class.java)
                        ?: snapshot.child("estados").getValue(Int::class.java)
                        ?: calcularEstadosPeloEndereco(
                            snapshot.child("endereco").child("estado").getValue(String::class.java)
                        )

                val fundacao = snapshot.child("fundacao").getValue(String::class.java) ?: ""
                val idade = calcularIdadeCooperativa(fundacao)

                val quantidadeProdutos = snapshot.child("Produtos").childrenCount.toInt()

                binding.txtEstadosAtuacao.text = estadosAtuacao.toString()
                binding.txtIdadeCooperativa.text = "$idade ANOS"
                binding.txtQuantidadeProdutos.text = quantidadeProdutos.toString()

                if (logoUrl.isNotBlank()) {
                    Glide.with(this)
                        .load(logoUrl)
                        .placeholder(R.drawable.fundo_cooperativa)
                        .error(R.drawable.fundo_cooperativa)
                        .circleCrop()
                        .into(binding.bg)

                    binding.bg.alpha = 0.22f
                } else {
                    binding.bg.setImageResource(R.drawable.fundo_cooperativa)
                    binding.bg.alpha = 0.22f
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar dados da cooperativa",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun contarCooperados(coopUid: String) {
        database.child("Usuarios")
            .get()
            .addOnSuccessListener { snapshot ->

                var totalCooperados = 0

                for (usuarioSnapshot in snapshot.children) {
                    val coopUidUsuario =
                        usuarioSnapshot.child("coopUid").getValue(String::class.java) ?: ""

                    val tipoUsuario =
                        usuarioSnapshot.child("tipoUsuario").getValue(String::class.java)
                            ?: usuarioSnapshot.child("tipo").getValue(String::class.java)
                            ?: ""

                    if (
                        coopUidUsuario == coopUid &&
                        tipoUsuario.lowercase() == "cliente"
                    ) {
                        totalCooperados++
                    }
                }

                binding.txtCooperados.text = totalCooperados.toString()
            }
            .addOnFailureListener {
                binding.txtCooperados.text = "0"

                Toast.makeText(
                    requireContext(),
                    "Erro ao contar cooperados",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun calcularEstadosPeloEndereco(estado: String?): Int {
        return if (estado.isNullOrBlank()) {
            0
        } else {
            1
        }
    }

    private fun calcularIdadeCooperativa(dataFundacao: String): Int {
        if (dataFundacao.isBlank()) return 0

        return try {
            val formatos = listOf(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            )

            var calendarioFundacao: Calendar? = null

            for (formato in formatos) {
                try {
                    val data = formato.parse(dataFundacao)

                    if (data != null) {
                        calendarioFundacao = Calendar.getInstance()
                        calendarioFundacao.time = data
                        break
                    }
                } catch (_: Exception) {
                }
            }

            if (calendarioFundacao == null) return 0

            val hoje = Calendar.getInstance()

            var idade = hoje.get(Calendar.YEAR) - calendarioFundacao.get(Calendar.YEAR)

            val mesAtual = hoje.get(Calendar.MONTH)
            val diaAtual = hoje.get(Calendar.DAY_OF_MONTH)

            val mesFundacao = calendarioFundacao.get(Calendar.MONTH)
            val diaFundacao = calendarioFundacao.get(Calendar.DAY_OF_MONTH)

            if (
                mesAtual < mesFundacao ||
                (mesAtual == mesFundacao && diaAtual < diaFundacao)
            ) {
                idade--
            }

            if (idade < 0) 0 else idade

        } catch (e: Exception) {
            0
        }
    }

    private fun configurarCliques() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}