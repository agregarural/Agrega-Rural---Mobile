package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryClickListeners()
        setupMenuClickListeners()
        setupBottomNavigation()
    }

    private fun setupCategoryClickListeners() {
        binding.cardPromocao.setOnClickListener { navigateToCategory("Promoção") }
        binding.cardMaisVendidos.setOnClickListener { navigateToCategory("Mais Vendidos") }
        binding.cardBovinos.setOnClickListener { navigateToCategory("Bovinos") }
        binding.cardSuinos.setOnClickListener { navigateToCategory("Suínos") }
        binding.cardAves.setOnClickListener { navigateToCategory("Aves") }
        binding.cardEquinos.setOnClickListener { navigateToCategory("Equinos") }
        binding.cardCaprinos.setOnClickListener { navigateToCategory("Caprinos") }
        binding.cardSementes.setOnClickListener { navigateToCategory("Sementes") }
        binding.cardDefensivos.setOnClickListener { navigateToCategory("Defensivos") }
        binding.cardFertilizantes.setOnClickListener { navigateToCategory("Fertilizantes") }
        binding.cardSacarias.setOnClickListener { navigateToCategory("Sacarias") }
        binding.cardCafeicultura.setOnClickListener { navigateToCategory("Cafeicultura") }
    }

    private fun setupMenuClickListeners() {
        binding.cardTrocarConta.setOnClickListener {
            Toast.makeText(requireContext(), "Trocar de conta", Toast.LENGTH_SHORT).show()
        }

        binding.cardDesconectar.setOnClickListener {
            showDesconectarDialog()
        }

        binding.cardSAC.setOnClickListener {
            Toast.makeText(requireContext(), "Atendimento ao cliente (SAC)", Toast.LENGTH_SHORT).show()
        }
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
            // Já está no Menu
        }
    }

    private fun navigateToCategory(categoria: String) {
        val bundle = Bundle()
        bundle.putString("CATEGORIA", categoria)
        findNavController().navigate(R.id.telaProdutoFragment, bundle)
    }

    private fun showDesconectarDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Desconectar")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sim") { _, _ ->
                Toast.makeText(requireContext(), "Desconectado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}