package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentLoginBinding

class TelaLoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btVoltar.setOnClickListener {
            requireActivity().finish()
        }

        binding.btEntrar.setOnClickListener {
            val intent = Intent(requireContext(), Home::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.recuperarTxt.setOnClickListener {
            findNavController().navigate(R.id.recuperarSenhaFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}