package com.mobile.agregarural.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mobile.agregarural.databinding.FragmentTelaCadastro4Binding
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.R
import com.mobile.agregarural.ui.common.MainActivity

class TelaCadastro4Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro4Binding? = null
    private val binding get() = _binding!!

    private val cadastroViewModel: CadastroViewModel by activityViewModels()

    private lateinit var auth : FirebaseAuth

    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaCadastro4Binding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btCadastrar.setOnClickListener {
           CadastrarUsuario()
        }
    }

    private fun CadastrarUsuario() {
        val senha = binding.cxNovasenha.text.toString().trim()
        val repitaSenha = binding.cxRepetir.text.toString().trim()
        val aceitouTermos = binding.termos.isChecked

        if (senha.isEmpty() || repitaSenha.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.preencha_os_campos_de_senha), Toast.LENGTH_SHORT).show()
            return
        }

        if (senha != repitaSenha) {
            Toast.makeText(requireContext(),
                getString(R.string.as_senhas_nao_coincidem), Toast.LENGTH_SHORT).show()
            return
        }

        if (!aceitouTermos) {
            Toast.makeText(requireContext(),
                getString(R.string.voce_precisa_aceitar_os_termos), Toast.LENGTH_SHORT).show()
            return
        }


        auth.createUserWithEmailAndPassword(cadastroViewModel.email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    val userMap = hashMapOf(
                        "uid" to userId,
                        "nome" to cadastroViewModel.nome,
                        "email" to cadastroViewModel.email,
                        "cpf" to cadastroViewModel.cpf,
                        "cep" to cadastroViewModel.cep,
                        "coopUid" to cadastroViewModel.coopUid,
                        "matricula" to cadastroViewModel.matricula,
                        "tipoUsuario" to cadastroViewModel.tipoUsuario
                    )

                    if (userId != null) {
                        database.reference.child("Usuarios").child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(),
                                    getString(R.string.cadastro_realizado_com_sucesso), Toast.LENGTH_SHORT).show()

                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(),
                                    getString(R.string.erro_ao_salvar_dados_adicionais), Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.erro_ao_cadastrar, task.exception?.message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}