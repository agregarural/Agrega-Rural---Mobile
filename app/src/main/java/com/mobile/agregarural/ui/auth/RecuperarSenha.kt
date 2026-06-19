package com.mobile.agregarural.ui.auth

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentRecuperarsenhaBinding

class RecuperarSenhaFragment : Fragment() {

    private var _binding: FragmentRecuperarsenhaBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecuperarsenhaBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btEnviar.setOnClickListener {
            efetuarRecuperacao()
        }
    }

    private fun efetuarRecuperacao() {

        val email = binding.cxRecuperar.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(requireContext(),
                getString(R.string.insira_o_seu_email2), Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mostrarPopupSucesso()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.erro_ao_enviar, task.exception?.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun mostrarPopupSucesso() {
        val popupView = layoutInflater.inflate(R.layout.popup_recuperar_senha, null)

        val popup = AlertDialog.Builder(requireContext())
            .setView(popupView)
            .create()

        popup.show()
        popup.window?.setDimAmount(0.7f)

        val btFechar = popupView.findViewById<Button>(R.id.btFechar)

        btFechar.setOnClickListener {
            popup.dismiss()
            findNavController().navigate(R.id.telaLoginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}