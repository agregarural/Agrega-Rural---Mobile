package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentTelaCadastro1Binding

class TelaCadastro1Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro1Binding? = null
    private val binding get() = _binding!!


    private val cadastroViewModel: CadastroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaCadastro1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()        }

        binding.btContinuar.setOnClickListener {
            validateData()
        }
    }

    private fun validateData(){
        val nome = binding.cxNome.text.toString().trim()
        val email = binding.cxEmail.text.toString().trim()
        if (nome.isNotBlank()){
            if(email.isNotBlank()){

                cadastroViewModel.nome = nome
                cadastroViewModel.email = email

                //Comentário temporário somente para testar a validação dos dados
                findNavController().navigate(R.id.action_telaCadastro1_to_telaCadastro2)

            }else{
                Toast.makeText(requireContext(), "Preencha seu email!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "Preencha seu nome!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}