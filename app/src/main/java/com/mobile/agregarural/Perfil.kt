package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardDadosPessoais.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_dadosPessoaisFragment)
        }

        binding.cardEnderecos.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_meusEnderecosFragment)
        }

        binding.cardCartoes.setOnClickListener {
            findNavController().navigate(R.id.action_perfil_to_meusCartoes)
        }

        binding.cardMeusPedidos.setOnClickListener {
            val intent = Intent(requireContext(), MeusPedidosActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}