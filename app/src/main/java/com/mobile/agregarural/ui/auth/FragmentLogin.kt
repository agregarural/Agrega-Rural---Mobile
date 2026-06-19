package com.mobile.agregarural.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentLoginBinding

class FragmentLoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btEntrar.setOnClickListener {
            validarEAutenticar()
        }

        binding.recuperarTxt.setOnClickListener {
            findNavController().navigate(R.id.recuperarSenhaFragment)
        }
    }

    private fun validarEAutenticar() {
        val email = binding.cxEmail.text.toString().trim()
        val senha = binding.cxSenha.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.insira_o_seu_email), Toast.LENGTH_SHORT).show()
            return
        }
        if (senha.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.insira_a_sua_senha), Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    verificarTipoUsuario()
                } else {
                    Toast.makeText(requireContext(),
                        getString(R.string.erro_ao_entrar, task.exception?.message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verificarTipoUsuario() {
        val uid = auth.currentUser?.uid ?: return

        val userRef = database.reference.child("Usuarios").child(uid)

        userRef.child("tipoUsuario").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val tipoUsuario = snapshot.value as? String

                if (tipoUsuario == "adm") {

                    findNavController().navigate(R.id.action_telaLoginFragment_to_homeFragment)
                } else {
                    findNavController().navigate(R.id.action_telaLoginFragment_to_homeFragment)
                }
            } else {
                findNavController().navigate(R.id.action_telaLoginFragment_to_homeFragment)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),
                getString(R.string.erro_ao_carregar_dados_do_perfil), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}