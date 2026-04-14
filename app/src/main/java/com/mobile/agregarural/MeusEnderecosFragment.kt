package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentMeusEnderecosBinding

class MeusEnderecosFragment : Fragment() {

    private var _binding: FragmentMeusEnderecosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusEnderecosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEnderecos()
        setupClickListeners()
    }

    private fun setupEnderecos() {
        binding.tvEndereco1.text = "Rua das Hortências, 228 – Centro\nMuniz Freire – ES, 29380-000"
        binding.tvEndereco2.text = "Avenida Rio Verde, 951 – Nova\nBrasilia Xique-Xique – BA,\n47400-000"
    }

    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEditarEndereco1.setOnClickListener {
            Toast.makeText(requireContext(), "Editar endereço 1", Toast.LENGTH_SHORT).show()
        }

        binding.btnEditarEndereco2.setOnClickListener {
            Toast.makeText(requireContext(), "Editar endereço 2", Toast.LENGTH_SHORT).show()
        }

        binding.cardEndereco1.setOnClickListener {
            Toast.makeText(requireContext(), "Endereço 1 selecionado", Toast.LENGTH_SHORT).show()
        }

        binding.cardEndereco2.setOnClickListener {
            Toast.makeText(requireContext(), "Endereço 2 selecionado", Toast.LENGTH_SHORT).show()
        }

        binding.adicionar.setOnClickListener {
            Toast.makeText(requireContext(), "Adicionar endereço", Toast.LENGTH_SHORT).show()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.navInicio.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.navCarrinho.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidade Carrinho em breve", Toast.LENGTH_SHORT).show()
        }

        binding.navEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.navMenu.setOnClickListener {
            val intent = Intent(requireContext(), Menu::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}