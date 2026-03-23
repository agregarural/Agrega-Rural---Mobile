package com.mobile.agregarural

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.agregarural.databinding.FragmentTelaPagamentoBinding
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
    ): View? {
        _binding = FragmentTelaPagamentoEnderecoBinding.inflate(layoutInflater)


        binding.btnConfirmar.setOnClickListener {
            val fragmentPagamento = TelaPagamento()

            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragmentPagamento)
                .addToBackStack(null)
                .commit()

        }





        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}