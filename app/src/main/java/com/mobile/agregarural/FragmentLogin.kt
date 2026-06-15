package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInserts ->
            val imeInserts = windowInserts.getInsets(WindowInsetsCompat.Type.ime())
            v.updatePadding(bottom = imeInserts.bottom)

            windowInserts
        }

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
            Toast.makeText(requireContext(), "Insira o seu e-mail!", Toast.LENGTH_SHORT).show()
            return
        }
        if (senha.isEmpty()) {
            Toast.makeText(requireContext(), "Insira a sua senha!", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Autentica o usuário com Email e Senha no Firebase Auth
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido no Auth, agora buscamos o papel (role) no banco de dados
                    verificarTipoUsuario()
                } else {
                    // Trata falhas comuns (senha errada, usuário não existe, etc.)
                    Toast.makeText(requireContext(), "Erro ao entrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verificarTipoUsuario() {
        val uid = auth.currentUser?.uid ?: return

        // 2. Busca o registro do usuário dentro do nó "Usuarios" usando o UID dele
        val userRef = database.reference.child("Usuarios").child(uid)

        userRef.child("tipoUsuario").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val tipoUsuario = snapshot.value as? String

                // 3. Condicional para direcionar o fluxo conforme o privilégio
                if (tipoUsuario == "adm") {

                    findNavController().navigate(R.id.action_telaLoginFragment_to_homeFragment)
                } else {
                    // Navega para a Home padrão do Usuário Comum
                    findNavController().navigate(R.id.action_telaLoginFragment_to_homeFragment)
                }
            } else {
                // Caso os dados adicionais não existam no banco, assume o fluxo padrão ou alerta o erro
                findNavController().navigate(R.id.action_telaLoginFragment_to_homeFragment)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Erro ao carregar dados do perfil.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}