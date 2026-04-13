package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentPerfilBinding // Ajuste para seu pacote

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

        // Mova a lógica que estava no onCreate da Activity para cá
        // Exemplo de como acessar os componentes via ViewBinding:

        binding.btnVoltar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cardDadosPessoais.setOnClickListener {
            val intent = Intent(requireContext(), DadosPessoais::class.java)
            startActivity(intent)
        }

        binding.cardEnderecos.setOnClickListener {
            val intent = Intent(requireContext(), MeusEnderecos::class.java)
            startActivity(intent)
        }

        binding.cardCartoes.setOnClickListener {
            val fragment = MeusCartoesFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
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