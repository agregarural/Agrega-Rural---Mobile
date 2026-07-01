package com.mobile.agregarural.ui.auth

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.mobile.agregarural.databinding.FragmentTelaCadastro3Binding

class TelaCadastro3Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro3Binding? = null
    private val binding get() = _binding!!

    private val cadastroViewModel: CadastroViewModel by activityViewModels()

    private lateinit var database: DatabaseReference

    private val cooperativasList = mutableListOf<Cooperativa>()
    private val cooperativasNomes = mutableListOf<String>()

    data class Cooperativa(
        val uid: String,
        val nome: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaCadastro3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        database = FirebaseDatabase.getInstance().reference

        carregarCooperativas()

        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btContinuar.setOnClickListener {
            validateData()
        }

        binding.checkAssociado.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cxCooperados.isEnabled = true
                binding.cxCooperados.alpha = 1f
            } else {
                binding.cxCooperados.text.clear()
                binding.cxCooperados.isEnabled = false
                binding.cxCooperados.alpha = 0.5f
            }
        }

        binding.cxCooperados.isEnabled = false
        binding.cxCooperados.alpha = 0.5f
    }

    private fun carregarCooperativas() {
        database.child(getString(com.mobile.agregarural.R.string.cooperativas))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    cooperativasList.clear()
                    cooperativasNomes.clear()

                    cooperativasList.add(Cooperativa("", getString(com.mobile.agregarural.R.string.selecione_uma_cooperativa)))
                    cooperativasNomes.add(getString(com.mobile.agregarural.R.string.selecione_uma_cooperativa))

                    for (child in snapshot.children) {
                        val uid = child.key ?: continue
                        val nome = child.child("nome").getValue(String::class.java) ?: continue

                        cooperativasList.add(Cooperativa(uid, nome))
                        cooperativasNomes.add(nome)
                    }

                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_item,
                        cooperativasNomes
                    )

                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                    binding.cxCoop.adapter = adapter

                    binding.cxCoop.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val cooperativaSelecionada = cooperativasList[position]

                                if (cooperativaSelecionada.uid.isNotEmpty()) {
                                    cadastroViewModel.cooperativa = cooperativaSelecionada.nome
                                    cadastroViewModel.coopUid = cooperativaSelecionada.uid
                                } else {
                                    cadastroViewModel.cooperativa = ""
                                    cadastroViewModel.coopUid = ""
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                cadastroViewModel.cooperativa = ""
                                cadastroViewModel.coopUid = ""
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        getString(com.mobile.agregarural.R.string.erro_ao_carregar),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun validateData() {
        val isAssociado = binding.checkAssociado.isChecked
        val matricula = binding.cxCooperados.text.toString().trim()

        when {
            cadastroViewModel.coopUid.isBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(com.mobile.agregarural.R.string.selecione_uma_cooperativa_valida),
                    Toast.LENGTH_SHORT
                ).show()
            }

            isAssociado && matricula.isBlank() -> {
                Toast.makeText(
                    requireContext(),
                    getString(com.mobile.agregarural.R.string.informe_sua_matricula),
                    Toast.LENGTH_SHORT
                ).show()
            }

            isAssociado -> {
                verificarMatriculaDuplicada(matricula)
            }

            else -> {
                cadastroViewModel.ehAssociado = false
                cadastroViewModel.matricula = ""

                findNavController().navigate(com.mobile.agregarural.R.id.action_telaCadastro3_to_telaCadastro4)
            }
        }
    }

    private fun verificarMatriculaDuplicada(matriculaDigitada: String) {
        binding.btContinuar.isEnabled = false

        database.child("Usuarios")
            .orderByChild("coopUid")
            .equalTo(cadastroViewModel.coopUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var matriculaJaExiste = false

                    for (usuarioSnapshot in snapshot.children) {
                        val matriculaExistente = usuarioSnapshot
                            .child("matricula")
                            .getValue(String::class.java)
                            ?.trim()

                        if (matriculaExistente == matriculaDigitada) {
                            matriculaJaExiste = true
                            break
                        }
                    }

                    binding.btContinuar.isEnabled = true

                    if (matriculaJaExiste) {
                        Toast.makeText(
                            requireContext(),
                            getString(com.mobile.agregarural.R.string.matricula_ja_existente),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        cadastroViewModel.ehAssociado = true
                        cadastroViewModel.matricula = matriculaDigitada

                        findNavController().navigate(com.mobile.agregarural.R.id.action_telaCadastro3_to_telaCadastro4)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.btContinuar.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        getString(com.mobile.agregarural.R.string.erro_ao_verificar_matricula),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}