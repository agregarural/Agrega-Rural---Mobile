package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentInicialBinding

class TelaInicial : Fragment() {

    private var _binding: FragmentInicialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btEntrar.setOnClickListener {
            findNavController().navigate(R.id.action_inicio_to_login)
        }

        binding.btCadastro.setOnClickListener {
            findNavController().navigate(R.id.action_inicio_to_cadastro)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}