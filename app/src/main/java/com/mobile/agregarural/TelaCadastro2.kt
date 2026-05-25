package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mobile.agregarural.databinding.FragmentTelaCadastro2Binding
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth

class TelaCadastro2Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro2Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

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

    private fun validateData(){
        val cpf = binding.cxCPF.text.toString().trim()
        val cep = binding.cxCEP.text.toString().trim()
        if (cpf.isNotBlank()){
            if(cep.isNotBlank()){
                //Comentário temporário somente para testar a validação dos dados
                findNavController().navigate(R.id.action_telaCadastro2_to_telaCadastro3)

            }else{
                Toast.makeText(requireContext(), "Preencha seu CEP!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "Preencha seu CPF!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}