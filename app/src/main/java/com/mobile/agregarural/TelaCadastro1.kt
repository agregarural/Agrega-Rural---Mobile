package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentTelaCadastro1Binding

class TelaCadastro1Fragment : Fragment() {

    private var _binding: FragmentTelaCadastro1Binding? = null
    private val binding get() = _binding!!

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
            findNavController().navigate(R.id.action_telaCadastro1_to_telaCadastro2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}