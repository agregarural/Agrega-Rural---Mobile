package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mobile.agregarural.databinding.FragmentTelaCadastro2Binding
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

class TelaCadastro2Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro2Binding? = null
    private val binding get() = _binding!!

    private val cadastroViewModel: CadastroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaCadastro2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btContinuar2.setOnClickListener {
            validateData()
        }
    }

    private fun validarCPF(cpf: String): Boolean {
        val c = cpf.replace(".", "").replace("-", "").trim()

        if (c.length != 11) return false
        if (c.all { it == c[0] }) return false // ex: 111.111.111-11

        // Primeiro dígito verificador
        var soma = 0
        for (i in 0..8) soma += c[i].digitToInt() * (10 - i)
        var resto = (soma * 10) % 11
        if (resto == 10 || resto == 11) resto = 0
        if (resto != c[9].digitToInt()) return false

        // Segundo dígito verificador
        soma = 0
        for (i in 0..9) soma += c[i].digitToInt() * (11 - i)
        resto = (soma * 10) % 11
        if (resto == 10 || resto == 11) resto = 0
        if (resto != c[10].digitToInt()) return false

        return true
    }

    private fun validateData() {
        val cpf = binding.cxCPF.text.toString().trim()
        val cep = binding.cxCEP.text.toString().trim()

        if (cpf.isBlank()) {
            Toast.makeText(requireContext(), "Preencha seu CPF!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!validarCPF(cpf)) {
            Toast.makeText(requireContext(), "CPF inválido!", Toast.LENGTH_SHORT).show()
            return
        }
        if (cep.isBlank()) {
            Toast.makeText(requireContext(), "Preencha seu CEP!", Toast.LENGTH_SHORT).show()
            return
        }

        // Valida CEP na API
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://viacep.com.br/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ViaCepService::class.java)

                val resposta = retrofit.buscarCep(cep.replace("-", ""))

                if (resposta.erro == true) {
                    Toast.makeText(requireContext(), "CEP não encontrado!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Tudo válido, segue o fluxo
                cadastroViewModel.cpf = cpf
                cadastroViewModel.cep = cep
                findNavController().navigate(R.id.action_telaCadastro2_to_telaCadastro3)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro ao verificar CEP!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}