package com.mobile.agregarural

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mobile.agregarural.databinding.FragmentTelaPagamentoEnderecoBinding


class TelaPagamentoEndereco: Fragment() {

    private  var _binding: FragmentTelaPagamentoEnderecoBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTelaPagamentoEnderecoBinding.inflate(layoutInflater)

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnConfirmar.setOnClickListener {
            findNavController().navigate(
                R.id.action_telaPagamentoEnderecoFragment_to_telaPagamentoFragment
            )
        }
        binding.btnEntrega.setOnClickListener {
            findNavController().navigate(R.id.meusPedidosFragment)
        }

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnmenu.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }





        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}