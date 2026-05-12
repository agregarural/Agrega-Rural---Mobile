package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.databinding.FragmentCarrinhoBinding

class CarrinhoFragment : Fragment() {

    private lateinit var binding: FragmentCarrinhoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCarrinhoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itens = listOf(
            ItemCarrinho("Ração Zardo", 49.90, 1),
            ItemCarrinho("Ração Premium2", 89.90, 2)
        )

        val adapter = CarrinhoAdapter(itens)

        binding.recyclerCarrinho.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        binding.btnFinalizarPedido.setOnClickListener {
            // lógica de finalização aqui
        }
    }
}
