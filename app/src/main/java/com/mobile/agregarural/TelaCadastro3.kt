package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.agregarural.databinding.FragmentTelaCadastro3Binding
import androidx.navigation.fragment.findNavController

class TelaCadastro3Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro3Binding? = null
    private val binding get() = _binding!!

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
            findNavController().navigate(R.id.action_telaCadastro3_to_telaCadastro4)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}