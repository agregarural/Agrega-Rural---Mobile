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


        setupMenuClickListeners()
        setupBottomNavigation()
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
        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }
        binding.cardatendimento.setOnClickListener {
            findNavController().navigate(R.id.TelaSacFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
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