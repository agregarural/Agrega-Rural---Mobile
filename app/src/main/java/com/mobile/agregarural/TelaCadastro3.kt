package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobile.agregarural.databinding.FragmentTelaCadastro3Binding
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class TelaCadastro3Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro3Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaCadastro3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.btContinuar.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val cooperativa = binding.cxCoop.text.toString().trim()
        val isAssociado = binding.checkAssociado.isChecked
        val matricula = binding.cxCooperados.text.toString().trim()

        when {
            cooperativa.isBlank() -> {
                Toast.makeText(requireContext(), "Informe o nome da cooperativa!", Toast.LENGTH_SHORT).show()
            }
            isAssociado && matricula.isBlank() -> {
                Toast.makeText(requireContext(), "Informe sua matrícula!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val bundle = Bundle().apply {
                    putString("cooperativa", cooperativa)
                    putBoolean("isAssociado", isAssociado)
                    putString("matricula", if (isAssociado) matricula else "")
                }
                findNavController().navigate(
                    R.id.action_telaCadastro3_to_telaCadastro4,
                    bundle
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}