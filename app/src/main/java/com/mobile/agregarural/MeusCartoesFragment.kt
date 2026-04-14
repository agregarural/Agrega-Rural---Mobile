package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentMeusCartoesBinding

class MeusCartoesFragment : Fragment() {

    private var _binding: FragmentMeusCartoesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeusCartoesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartoes()
        setupClickListeners()
    }

    private fun setupCartoes() {
        binding.tvCartao1Info.text =
            "Número: **** 4012, Nome:\nMURILO GOMES, Validade: 01/27,\nCVV: ***"

        binding.tvCartao2Info.text =
            "Número: **** 8907, Nome:\nMURILO G C, Validade: 10/28,\nCVV: ***"
    }

    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEditarCartao1.setOnClickListener {
            showToast("Editar cartão 1")
        }

        binding.btnEditarCartao2.setOnClickListener {
            showToast("Editar cartão 2")
        }

        binding.cardCartao1.setOnClickListener {
            showToast("Cartão 1 selecionado")
        }

        binding.cardCartao2.setOnClickListener {
            showToast("Cartão 2 selecionado")
        }

        binding.adicionar.setOnClickListener {
            showToast("Adicionar cartão")
        }

        binding.navInicio.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.navCarrinho.setOnClickListener {
            showToast("Funcionalidade Carrinho em breve")
        }

        binding.navEntrega.setOnClickListener {
            val intent = Intent(requireContext(), MeusPedidosFragment::class.java)
            startActivity(intent)
        }

        binding.navMenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
    }

    private fun showToast(mensagem: String) {
        Toast.makeText(requireContext(), mensagem, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}